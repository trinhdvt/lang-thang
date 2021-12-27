# LangThang - Travel blogs website ✈️🏞

## Table of Contents

- [Overview](#overview)
- [Tech Stack](#tech-stack)
- [Demo](#demo)
- [Run locally](#run-locally)
- [To-do](#to-do)
- [Authors](#authors)
- [Documentation](#documentation)
- [Rate Limit Policy](#rate-limit-policy)
- [Contact](#contact)

## Overview

This project is a website where people can join and share their travel experiences with others people. In this website,
user can write their own post and publish it, make an interaction by writing comments in someone's posts or bookmark it
if they like. They can also follow others user to receive notifications when there is something new.

## Tech Stack

**Client:** ReactJS, Redux, Bootstrap

**Server:** Spring, Hibernate

**Storage Services:** MySQL, AWS S3, AWS Cloudfront

**Deployment:** Docker, AWS

## Demo

Here is a link to the deployed website on AWS EC2: [LangThang](https://langthang.tech)

![landing_page](https://i.imgur.com/vU7jhua.png)

![detail_post_page](https://i.imgur.com/6IRw6fX.png)

## Run locally

1. Install Docker and Docker compose

> If you haven't installed Docker and Docker compose yet, follow these instructions at [here](https://docs.docker.com/engine/install) and [here](https://docs.docker.com/compose/install/)

2. Clone this repo

```
git clone https://github.com/trinhdvt/lang-thang.git
```

3. Go to the project directory

```
cd lang-thang/
```

4. Start the server

```
docker compose up
```

> To stop server, you may want to run `docker compose down` instead of `Ctrl + C`

By default, server is running at `http://localhost` on port `80`. But if you want to run server on other port, change
the following setting in `docker-compose.yml` file:

* To change the server's port, edit the following line `"80:80"` to `"{YOUR_PORT}:80"`. Make sure you just edit the
  number **before the colon** and keep the second number is **80** as default.

* In case you run this on a cloud server or remote server, you may want to edit the following
  line `HOST: http://localhost` to `HOST: http://{YOUR_HOST_NAME}` .

And restart server with `docker compose up` when everything done!

## To-do

* Implement backend APIs use GraphQL

* Change database platform to MongoDB

## Authors

- [@dvt](https://github.com/trinhdvt) - Backend Developer, Semi-DevOps

- [@lcko](https://github.com/lcko1012) - Frontend Developer

- [@ntdung141](https://github.com/NTDung141) - Frontend Developer

- [@pvt](https://github.com/phamvantanh) - Business Analysis

## Documentation

[API Document](./docs/API)

[Frontend Repository](https://github.com/lcko1012/travel-blog)

## Rate Limit Policy

> For more detail, please read [here](./docs/README.md#rate-limit-policy)

## Contact

If you want to build this project from source, feel free to contact me [@dvt](https://www.facebook.com/trinh.dvt/)