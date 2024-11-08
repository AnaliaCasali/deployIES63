# Development stage
#for dev
FROM node:18-alpine AS  angular_app
WORKDIR /app
COPY package*.json ./
ENV NODE_OPTIONS="--max_old_space_size=4096"
RUN npm install -g @angular/cli@18.0.0  && npm ci
COPY . .
RUN npm run build --verbose
#CMD ["npm", "start"]
CMD ["ng", "serve", "--host", "0.0.0.0"]


#for prod
FROM node:18-alpine as build
WORKDIR /app
COPY . .
RUN npm install && npm ci
# Use npx directly to avoid npm script issues
RUN npx ng build ISP63_FrontEnd2024 --configuration=production

# Serve Application using Nginx Server
FROM nginx:alpine AS angular_app_prod
COPY --from=build /app/dist/isp63-front-end2024/ /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
