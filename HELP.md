# Getting Started

### Reference Documentation
For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/3.5.13/maven-plugin)
* [Create an OCI image](https://docs.spring.io/spring-boot/3.5.13/maven-plugin/build-image.html)
* [Spring Web](https://docs.spring.io/spring-boot/3.5.13/reference/web/servlet.html)
* [Thymeleaf](https://docs.spring.io/spring-boot/3.5.13/reference/web/servlet.html#web.servlet.spring-mvc.template-engines)
* [Spring Data JPA](https://docs.spring.io/spring-boot/3.5.13/reference/data/sql.html#data.sql.jpa-and-spring-data)
* [Spring Boot DevTools](https://docs.spring.io/spring-boot/3.5.13/reference/using/devtools.html)
* [Validation](https://docs.spring.io/spring-boot/3.5.13/reference/io/validation.html)

### Guides
The following guides illustrate how to use some features concretely:

* [Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/)
* [Serving Web Content with Spring MVC](https://spring.io/guides/gs/serving-web-content/)
* [Building REST services with Spring](https://spring.io/guides/tutorials/rest/)
* [Handling Form Submission](https://spring.io/guides/gs/handling-form-submission/)
* [Accessing Data with JPA](https://spring.io/guides/gs/accessing-data-jpa/)
* [Accessing data with MySQL](https://spring.io/guides/gs/accessing-data-mysql/)
* [Validation](https://spring.io/guides/gs/validating-form-input/)

### Maven Parent overrides

Due to Maven's design, elements are inherited from the parent POM to the project POM.
While most of the inheritance is fine, it also inherits unwanted elements like `<license>` and `<developers>` from the parent.
To prevent this, the project POM contains empty overrides for these elements.
If you manually switch to a different parent and actually want the inheritance, you need to remove those overrides.

## Deploy On Render

This project is now ready for deployment on Render using the `render.yaml` file at the project root.

### 1. Push code to GitHub

Commit and push your latest changes, including `render.yaml`.

### 2. Create Web Service

- In Render dashboard, choose **New +** -> **Blueprint**.
- Connect your GitHub repository.
- Render will detect and use `render.yaml` automatically.

### 3. Set database environment variables

Set the following variables in Render (Environment tab):

- `DB_URL` (example: `jdbc:mysql://<host>:3306/<database>?useSSL=true&allowPublicKeyRetrieval=true`)
- `DB_USER`
- `DB_PASS`

### 4. Optional runtime flags

Defaults are already configured in `application.properties`, but you can override:

- `JPA_DDL_AUTO` (default `update`)
- `JPA_SHOW_SQL` (default `false`)
- `JPA_DEFER_INIT` (default `false`)
- `SQL_INIT_MODE` (default `never`)

If you want to load seed data from `data.sql` on first deploy, temporarily set:

- `SQL_INIT_MODE=always`

After data is loaded, set it back to `never`.

