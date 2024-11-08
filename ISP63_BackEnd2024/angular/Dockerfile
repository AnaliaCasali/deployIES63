#for dev
FROM node:18-alpine AS angular_app
WORKDIR /app
COPY package*.json ./
ENV NODE_OPTIONS="--max_old_space_size=4096"
RUN npm install -g @angular/cli@18.0.0  && npm ci
COPY . .
RUN npm run build --verbose
#CMD ["npm", "start"]
CMD ["ng", "serve", "--host", "0.0.0.0"]






#for prod
#FROM node:16-alpine AS build
#WORKDIR /app

#COPY . .
#RUN npm install
#RUN npm run build
# Serve Application using Nginx Server
#FROM nginx:alpine
#COPY --from=build /app/dist/project-name/ /usr/share/nginx/html
#EXPOSE 80
