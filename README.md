# rabbitmq-demo
An implementation of a retry queue using dlq on rabbitmq

## Requeriments
```bash
git clone https://github.com/devjony/rabbitmq-demo.git && \
  cd rabbitmq-demo && \
  docker-compose up -d rabbitmq && \
  mvn clean install
```

## Run Locally
```bash
mvn spring-boot:run
```

## Rabbitmq management
```bash
http://localhost:15672/
```

## License
[MIT](https://choosealicense.com/licenses/mit/)