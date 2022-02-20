FROM openjdk:11
COPY . /usr/src/vendingmachine
WORKDIR /usr/src/vendingmachine
RUN javac Main.java
CMD ["java", "Main"]
