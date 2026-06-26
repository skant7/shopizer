# Shopizer 3 (Java 11 / 17+)

**3.2.7**

[![last_version](https://img.shields.io/badge/last_version-v3.2.7-blue.svg?style=flat)](https://github.com/shopizer-ecommerce/shopizer/tree/3.2.7)
[![Official site](https://img.shields.io/website-up-down-green-red/https/shields.io.svg?label=official%20site)](http://www.shopizer.com/)
[![Docker Pulls](https://img.shields.io/docker/pulls/shopizerecomm/shopizer.svg)](https://hub.docker.com/r/shopizerecomm/shopizer)
[![stackoverflow](https://img.shields.io/badge/shopizer-stackoverflow-orange.svg?style=flat)](http://stackoverflow.com/questions/tagged/shopizer)
[![CircleCI](https://circleci.com/gh/shopizer-ecommerce/shopizer.svg?style=svg)](https://circleci.com/gh/shopizer-ecommerce/shopizer)

Java open source e-commerce software.

Headless commerce and REST API for ecommerce:

- Catalog
- Shopping cart
- Checkout
- Merchant
- Order
- Customer
- User

Access the headless API: <http://localhost:8080/swagger-ui.html>

## Demo

See the demo: **New demo on the way 2025** — headless demo available soon.

## Run with Docker

### 1. Backend (Java API)

```bash
docker run -p 8080:8080 shopizerecomm/shopizer:latest
```

### 2. Administration tool

Requires the Java backend to be running.

```bash
docker run \
  -e "APP_BASE_URL=http://localhost:8080/api" \
  -p 82:80 shopizerecomm/shopizer-admin
```

### 3. React shop sample site

Requires the Java backend to be running.

```bash
docker run \
  -e "APP_MERCHANT=DEFAULT" \
  -e "APP_BASE_URL=http://localhost:8080" \
  -p 80:80 shopizerecomm/shopizer-shop-reactjs
```

## Get the source code

```bash
git clone git://github.com/shopizer-ecommerce/shopizer.git
```

## Build the application

### 1. Shopizer backend

```bash
cd shopizer
./mvnw clean install
cd sm-shop
./mvnw spring-boot:run
```

### 2. Shopizer admin

For compiling and running Shopizer admin, consult the admin repository README.

### 3. Shop sample site

For compiling and running the shop sample site, consult that repository’s README.

## Access the application

Headless web application (Swagger UI): <http://localhost:8080/swagger-ui.html>

The steps above run the application with default settings. See the project documentation for connecting to MySQL, configuring email, and other subsystems.

## Documentation

- Documentation: [shopizer-ecommerce.github.io/documentation](https://shopizer-ecommerce.github.io/documentation/)
- API (local Swagger UI): <http://localhost:8080/swagger-ui/index.html>
- Slack: [shopizer.slack.com](https://shopizer.slack.com) — join via [Community Inviter](https://communityinviter.com/apps/shopizer/shopizer)
- Website: [shopizer.com](http://www.shopizer.com)

## Participation

If you want to give feedback or participate in the Shopizer project, use the [contact form](http://www.shopizer.com/contact.html) and share your email so we can invite you to Slack.

## How to contribute

1. Fork the repository to your GitHub account.

2. Clone your fork:

   ```bash
   git clone https://github.com/yourusername/shopizer.git
   ```

3. Build the application using the steps above.

4. Create a feature branch:

   ```bash
   git checkout -b branch-name
   ```

5. Push your changes and open a pull request against the upstream repository so your work can be reviewed and merged.
