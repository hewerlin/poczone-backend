package net.poczone.framework.servlet;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import net.poczone.framework.defaults.FrameworkErrorCodes;
import net.poczone.framework.defaults.PropertiesLoca;
import net.poczone.framework.definitions.Application;
import net.poczone.framework.definitions.context.Database;
import net.poczone.framework.definitions.context.Loca;
import net.poczone.framework.definitions.operations.ErrorCodeException;
import net.poczone.framework.definitions.operations.Input;
import net.poczone.framework.definitions.operations.Operation;
import net.poczone.framework.tools.db.JdbcDatabase;
import net.poczone.framework.tools.templates.TemplateBatch;
import net.poczone.framework.tools.templates.TemplateEngine;
import net.poczone.framework.tools.templates.TemplateInstance;

public class ApplicationServlet extends HttpServlet {
	private static final long serialVersionUID = 3490110721916831073L;

	private String path;
	private Application application;
	private Loca loca;

	@Override
	public void init(ServletConfig config) throws ServletException {
		String appParam = config.getInitParameter("application");
		path = config.getInitParameter("path");

		if (appParam == null) {
			throw new ServletException("Missing parameter application");
		} else if (path == null) {
			throw new ServletException("Missing parameter path");
		}

		try {
			Class<?> clazz = Class.forName(appParam);
			application = (Application) clazz.newInstance();
			loca = new PropertiesLoca().load(clazz);
		} catch (Exception e) {
			throw new ServletException("Failed to initialize application", e);
		}
	}

	@Override
	protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String relPath = getPath(req);
		Operation operation = getOperation(relPath);
		String origin = req.getHeader("Origin");

		if (origin != null && operation != null && application.acceptsOrigin(operation, origin)) {
			setAccessControlHeaders(resp, origin);
		}
	}

	private void setAccessControlHeaders(HttpServletResponse resp, String origin) {
		resp.setHeader("Access-Control-Allow-Origin", origin);
		resp.setHeader("Access-Control-Allow-Methods", "POST");
	}

	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String relPath = getPath(req);

		if ("".equals(relPath)) {
			printExplorer(req, resp);
			return;
		}

		Operation operation = getOperation(relPath);
		if (operation != null) {
			redirectToForm(req, resp, operation);
		} else {
			resp.sendError(404);
		}
	}

	private String getPath(HttpServletRequest req) throws IOException {
		String relPath = req.getRequestURI();
		if (relPath.startsWith(req.getServletContext().getContextPath())) {
			relPath = relPath.substring(req.getServletContext().getContextPath().length());
		}
		if (relPath.startsWith(path)) {
			relPath = relPath.substring(path.length());
		} else {
			throw new IOException("Invalid path");
		}
		return relPath;
	}

	private Operation getOperation(String relPath) {
		for (Operation operation : application.getOperations()) {
			if (relPath.equals(operation.getName())) {
				return operation;
			}
		}
		return null;
	}

	private void printExplorer(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String lang = getLang(req);

		File templateRoot = new File(req.getServletContext().getRealPath("WEB-INF/templates/application-explorer"));
		TemplateEngine engine = new TemplateEngine(templateRoot);

		TemplateInstance index = engine.newInstance("index.htm");
		TemplateBatch operationIndexItems = engine.newBatch("operation-index-item.htm");
		TemplateBatch operationForms = engine.newBatch("operation-form.htm");
		TemplateBatch operationFormInputs = engine.newBatch("operation-form-input.htm");
		TemplateBatch languages = engine.newBatch("lang.htm");

		for (Operation operation : application.getOperations()) {
			String relPath = operation.getName();
			String locaName = loca.get(lang, relPath, "");

			operationFormInputs.reset();
			for (Input<?> input : operation.getInputs()) {
				operationFormInputs.put("name", input.getName());
				operationFormInputs.put("locaName", loca.get(lang, relPath + "/" + input.getName(), ""));
				operationFormInputs.addBatch();
			}

			operationIndexItems.put("relPath", relPath);
			operationIndexItems.put("locaName", locaName);
			operationIndexItems.addBatch();

			operationForms.put("relPath", relPath);
			operationForms.put("locaName", locaName);
			operationForms.put("inputs", operationFormInputs);
			operationForms.put("outputs", operation.getOutputs());
			operationForms.put("errorCodes", operation.getErrorCodes());
			operationForms.addBatch();
		}

		index.put("locaName", loca.get(lang, "app/name", "Operation Explorer"));
		index.put("introName", loca.get(lang, "app/introName", "Intro"));
		index.put("introHTML", loca.get(lang, "app/introHTML", ""));
		index.put("description", loca.get(lang, "app/description", ""));

		index.put("operationForms", operationForms);
		index.put("operationIndexItems", operationIndexItems);

		for (String language : loca.getLanguages()) {
			languages.put("lang", language);
			languages.addBatch();
		}
		index.put("languages", languages);

		resp.setContentType("text/html; charset=UTF-8");
		resp.getOutputStream().write(index.toUTF8());
	}

	private String getLang(HttpServletRequest req) {
		List<String> languages = loca.getLanguages();

		String lang = req.getParameter("lang");
		if (lang != null && languages.contains(lang)) {
			return lang;
		}

		String header = req.getHeader("Accept-Language");
		if (header != null) {
			String[] parts = header.split(";|,");
			for (String part : parts) {
				if (part.matches("[a-z]{2}") && languages.contains(part)) {
					return part;
				}
			}
		}

		return "en";
	}

	private void redirectToForm(HttpServletRequest req, HttpServletResponse resp, Operation operation)
			throws IOException {
		resp.sendRedirect(req.getContextPath() + path + "#" + operation.getName());
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try (ExecutionContextImpl context = new ExecutionContextImpl()) {
			String relPath = getPath(req);
			Operation operation = getOperation(relPath);
			if (operation == null) {
				context.addError(new ErrorCodeException(FrameworkErrorCodes.OPERATION_NOT_FOUND));
			}

			boolean originOk = true;
			String origin = req.getHeader("Origin");
			if (!context.hasErrors()) {
				originOk = origin == null || application.acceptsOrigin(operation, origin);
				if (!originOk) {
					context.addError(new ErrorCodeException(FrameworkErrorCodes.ORIGIN_DENIED));
				}
			}

			if (!context.hasErrors()) {
				context.setRoot(new File(req.getServletContext().getRealPath("")));
				context.setDatabase(buildDatabase(req.getServletContext()));
				context.setLoca(loca);

				List<Input<?>> inputs = operation.getInputs();
				checkAndParseParameters(req, inputs, context);
			}

			if (!context.hasErrors()) {
				try {
					operation.execute(context);
				} catch (Exception e) {
					context.addError(mapException(e));
				}
			}

			JSONObject result = context.hasErrors() ? context.getFailureResult() : context.getSuccessResult();

			if (origin!=null && originOk) {
				setAccessControlHeaders(resp, origin);
			}

			resp.setStatus(context.getHttpStatus());
			resp.setContentType("application/json; charset=UTF-8");
			resp.getOutputStream().write(result.toString(1).getBytes("UTF-8"));
		}
	}

	private void checkAndParseParameters(HttpServletRequest req, List<Input<?>> inputs, ExecutionContextImpl context) {
		for (Input<?> parameter : inputs) {
			String value = req.getParameter(parameter.getName());

			ErrorCodeException ex = null;
			if (value == null) {
				ex = new ErrorCodeException(FrameworkErrorCodes.MISSING_PARAMETER, parameter.getName());
			} else {
				try {
					Object object = parameter.parse(value);
					context.set(parameter, object);
				} catch (ErrorCodeException e) {
					ex = e;
				} catch (Exception e) {
					ex = new ErrorCodeException(FrameworkErrorCodes.INVALID_PARAMETER_VALUE, parameter.getName());
				}
			}

			if (ex != null) {
				context.addError(ex);
			}
		}
	}

	private ErrorCodeException mapException(Exception e) {
		if (e instanceof ErrorCodeException) {
			return (ErrorCodeException) e;
		}

		e.printStackTrace();

		if (e instanceof IOException) {
			return new ErrorCodeException(FrameworkErrorCodes.IO_EXCEPTION);
		} else if (e instanceof SQLException) {
			return new ErrorCodeException(FrameworkErrorCodes.DATABASE_EXCEPTION);
		} else {
			return new ErrorCodeException(FrameworkErrorCodes.INTERNAL_EXCEPTION);
		}
	}

	private Database buildDatabase(ServletContext ctx) {
		String url = ctx.getInitParameter("db.url");
		String username = ctx.getInitParameter("db.username");
		String password = ctx.getInitParameter("db.password");

		return new JdbcDatabase(url, username, password);
	}
}
