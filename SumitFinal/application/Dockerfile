FROM node:12
RUN mkdir upg-loadme-app
COPY  upg-loadme-app /upg-loadme-app
WORKDIR /upg-loadme-app
#RUN cd /upg-loadme-app
RUN npm install
RUN npm build
CMD ["npm", "start"]
 
