FROM node:slim

WORKDIR /usr/src/client-docgenerator

COPY package*.json ./

RUN npm install

COPY . .

EXPOSE 3000

CMD ["yarn","start","run"]