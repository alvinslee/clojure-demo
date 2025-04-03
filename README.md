# Prime Number Explorer

A simple Clojure web application that helps you explore prime numbers. Built with:
- Clojure
- Jetty (HTTP server)
- Hiccup (HTML generation)

## Features
- Check if a number is prime
- View the first 50 prime numbers
- Simple, responsive Bootstrap interface

## Development

### Prerequisites
- Java 21
- Leiningen

### Running Locally
```bash
lein run
```
Then visit http://localhost:3000 in your browser.

### Building
To create a standalone JAR:
```bash
lein uberjar
```

### Deployment
This application is ready to deploy to Heroku. Make sure you have:
- `Procfile`
- `system.properties` (configured for Java 21)
- Heroku CLI installed

Deploy with:
```bash
git push heroku main
```

## License
[Your chosen license] 