FROM node:alpine

WORKDIR /app

COPY package.json .

RUN npm install

COPY . .

RUN npm run build

EXPOSE 3000

ENTRYPOINT [ "node_modules/.bin/serve", "-s", "dist" ]