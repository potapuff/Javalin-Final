# CV Site with SQL Injection Demo

This is a demonstration project showing how SQL injection vulnerabilities can occur in web applications. **This code is intentionally vulnerable and should NOT be used in production.**

## Features
- Simple CV/personal page with pixel art style
- Markdown content support
- SQLite database
- Basic authentication
- Pixel art avatars and error pages
- Intentional SQL injection vulnerability for educational purposes

## Prerequisites
- Java 17 or higher
- Maven
- SQLite

## Environment Setup

The application requires the following environment variables to be set:
```
export CV_ADMIN_USERNAME=admin
export CV_ADMIN_PASSWORD=changeme
```

Alternatively, create a `.env` file in the project root:
```
CV_ADMIN_USERNAME=admin
CV_ADMIN_PASSWORD=changeme
```
## Building and Running

1. Clone the repository:
```
git clone https://github.com/yourusername/cv-site.git
cd cv-site
```

2. Build the application:
```
mvn clean package
```

3. Run the application:

   a. Using the run script (recommended):
   ```
   chmod +x run.sh
   ./run.sh
   ```

   b. Manually with environment variables:
   ```
   export CV_ADMIN_USERNAME=admin
   export CV_ADMIN_PASSWORD=changeme
   java -jar target/cv-site-1.0-SNAPSHOT.jar
   ```

4. Access the application:
   - Main page: `http://localhost:7070`
   - Edit mode: `http://localhost:7070/?edit`


## SQL Injection Vulnerability

The application contains intentional SQL injection vulnerabilities in the ContentDao class for educational purposes.

### Vulnerable Code Example
```
// DON'T DO THIS IN PRODUCTION!
String sql = "UPDATE content SET content = '" + content + "' WHERE id = '" + id + "'";
conn.createStatement().executeUpdate(sql);
```

### How to Test SQL Injection

1. Access edit mode: `http://localhost:7070/?edit`
2. Click any "Edit" button
3. Login with configured credentials
4. Try these SQL injection payloads:

#### Content Manipulation

```
new content'); UPDATE content SET content = 'Hacked!' WHERE id != ('x
```
or
```
new content'); DROP TABLE content; --
```

### Secure Alternative
Here's how the code should be written to prevent SQL injection:

```
// DO THIS INSTEAD
PreparedStatement stmt = conn.prepareStatement(
"UPDATE content SET content = ? WHERE id = ?"
);
stmt.setString(1, content);
stmt.setString(2, id);
stmt.executeUpdate();
```

## Features Details

### Content Management
- Markdown support for content
- Edit interface with preview
- Sections: About, Skills, Experience

### Security (Intentionally Vulnerable)
- Basic Authentication
- Environment-based credentials
- SQL injection vulnerability for demonstration

### UI Features
- Pixel art style design
- Responsive layout
- Custom error pages
- Dynamic avatar generation
- Bootstrap integration

## Development Notes

### Adding New Content Sections
1. Add entry to `insertDefaultContent()` in ContentDao
2. Content will be automatically added on first run

### Customizing Appearance
1. Modify `styles.css` for visual changes
2. Update pixel art in AvatarGenerator
3. Customize error page dinosaurs

## Security Warning

**This application contains intentional security vulnerabilities for educational purposes. DO NOT USE IN PRODUCTION!**

Common security issues demonstrated:
1. SQL Injection vulnerability
2. Basic Authentication (instead of more secure methods)
3. Direct database access without ORM
4. Minimal input validation

## Contributing

1. Fork the repository
2. Create your feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Acknowledgments
- Javalin web framework
- Thymeleaf templating
- Bootstrap CSS framework
