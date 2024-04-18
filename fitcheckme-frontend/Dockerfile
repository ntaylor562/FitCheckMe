FROM node:21-alpine3.18

WORKDIR /app

COPY package.json .
COPY package-lock.json .

RUN npm ci

COPY . .

RUN npm run build

EXPOSE 3000

ENTRYPOINT node_modules/.bin/serve -s dist