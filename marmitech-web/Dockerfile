FROM node:20-alpine AS build
WORKDIR /app

# Copia manifests de pacotes primeiro para cache das camadas
COPY package*.json ./
RUN npm ci

# Copia o codigo-fonte
COPY . .

# Gera o build de producao (gera em dist/marmitech-web/browser)
RUN npm run build -- --configuration production


FROM nginx:alpine

# Limpa configuracoes padroes do Nginx
RUN rm -rf /etc/nginx/conf.d/*

# Copia configuracao personalizada do Nginx (SPA + proxy reverso)
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Copia os arquivos compilados do Angular
COPY --from=build /app/dist/marmitech-web/browser /usr/share/nginx/html

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
