#FROM node:lts-alpine as builder

#WORKDIR /app
#ARG SOURCE_FOLDER=travel-blog

#COPY ${SOURCE_FOLDER}/package.json .
#COPY ${SOURCE_FOLDER}/package-lock.json .

#RUN npm install --legacy-peer-deps

#COPY ${SOURCE_FOLDER}/src src
#COPY ${SOURCE_FOLDER}/public public

#RUN npm run build

FROM nginx:alpine

WORKDIR /app
#ARG BUILD_FOLDER=/app/build
ARG SOURCE_FOLDER=travel-blog
COPY ${SOURCE_FOLDER}/build /usr/share/nginx/build
#COPY --from=builder ${BUILD_FOLDER} /usr/share/nginx/build

COPY nginx/nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

ENTRYPOINT [ "nginx","-g","daemon off;" ]
