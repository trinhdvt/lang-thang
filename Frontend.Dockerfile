FROM node:14.14-alpine3.12 as builder

WORKDIR /app
ARG SOURCE_FOLDER=frontend-travel-blog

COPY ${SOURCE_FOLDER}/package.json .

RUN npm install --legacy-peer-deps --only=production

COPY ${SOURCE_FOLDER}/src src
COPY ${SOURCE_FOLDER}/public public

RUN npm run build

FROM nginx:alpine

WORKDIR /app
ARG BUILD_FOLDER=/app/build

COPY --from=builder ${BUILD_FOLDER} /usr/share/nginx/build

COPY nginx/nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

ENTRYPOINT [ "nginx","-g","daemon off;" ]