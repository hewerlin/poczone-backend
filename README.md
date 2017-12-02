# poczone-backend
POCZone.net Backend Service for Shared Spaces with Live JSON Data Storage

The POCZone.net Backend Service enables user authentication, space management and sharing and JSON live data access.

An Online Instance is hosted at https://poczone.net/backend/.

[An introduction post was published at Medium.](https://medium.com/@takehomemessage/shared-app-spaces-with-live-json-data-d7c5e2d71070)

# Current Operation Index

These are the operations currently supported:

- auth/register
  = Register user
- auth/login
  = Login user
- auth/logout
  = Logout session
- spaces/create
  = Create space
- spaces/getMine
  = Get my spaces
- spaces/edit
  = Edit space
- spaces/share
  = Share space / Add collaborator
- spaces/leave
  = Leave space
- data/token/create
  = Grant space data access
- data/token/revoke
  = Revoke space data access
- data/json/post
  = Commit JSON space data diff
- data/json/getDiff
  = Get JSON space data diff
- data/json/getByIDs
  = Get JSON space data by IDs
  
# Install Instructions

- Install Servlet Container (e.g. Tomcat 8) and Database (e.g. MySQL)
- Set context parameters db.url (="jdbc:mysql://{host}/{database}"), db.username, db.password (for Tomcat, it's in the context.xml file).
- Run MySQL Init Script [doc/poczone-backend-init.sql](doc/poczone-backend-init.sql)

- Clone this repository
- Add MySQL JDBC Connector JAR to WEB-INF/lib
- Add one of these JSON JARs to WEB-INF/lib
  - https://github.com/poczone/JSON-java = fork with ordered JSON keys
  - https://github.com/stleary/JSON-java = original implementation with "random" JSON key order based on HashMap
- Build WAR
- Deploy to Servlet Container
