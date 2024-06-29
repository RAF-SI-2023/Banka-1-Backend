# Ovo je multi-stage Docker build: https://docs.docker.com/build/building/multi-stage/
# Multi-stage build nam omogucava da koristimo jedan image koji ima sve alate
# da buildujemo aplikaciju, a drugi image kao osnovu za kranji image koji buildujemo.
#
# U prvom stage-u koristimo Maven image koji dolazi sa mavenom i Javom 17.
# Tu buildujemo aplikaciju i pravimo .jar fajl.
# U drugom stage-u koristimo OpenJDK image koji ima samo Javu sto nam je
# dovoljno da pokrenemo aplikaciju.
#
# Ovo je univerzalni Dockerfile koji moze da se koristi za sve Java/Spring mikroservise.
FROM harbor.k8s.elab.rs/base-images/base:java-21-node-18-docker AS builder

# Ovaj deo definise HOME direktorijum koji se koristi kao radni direktorijum prilikom buildovanja.
ENV HOME=/usr/app
RUN mkdir -p $HOME
WORKDIR $HOME

# Ovaj deo kopira mikroservis u image kako bi Maven mogao da builduje taj mikroservis.
ADD . $HOME

COPY . $HOME

# Ovaj deo builduje .jar fajl.
# - "--mount=type=cache,target=/root/.m2" sluzi da kesira maven dependencies
#   kako ih ne bi iznova i iznova preuzimali prilikom svakog buildovanja image-a
# - "-Drevision=$VERSION" - prosledjuje verziju koju smo definisali gore, ignorisati
# - "-Dmaven.test.skip" - iskljucuje pokretanje testova prilikom buildovanja image-a
# - "-f $HOME/pom.xml" - putanja do pom.xml fajla
RUN --mount=type=cache,target=/root/.m2 mvn -DfinalName=app -Dmaven.test.skip -Dcheckstyle.skip=true -f $HOME/pom.xml clean package

# Ovo je drugi stage gde dobijamo rezultirajuci image.
# Ovde kopiramo .jar fajl napravljen u prethodnom, tj. prvom stage-u.
FROM openjdk:23

RUN microdnf install dnf

RUN dnf install -y https://dl.fedoraproject.org/pub/epel/epel-release-latest-8.noarch.rpm && \
    dnf install -y \
    wget \
    gnupg \
    unzip \
    https://dl.google.com/linux/chrome/rpm/stable/x86_64/google-chrome-stable-126.0.6478.126-1.x86_64.rpm && \
    dnf clean all

# Download and install ChromeDriver
RUN CHROME_DRIVER_VERSION=$(curl -sS chromedriver.storage.googleapis.com/LATEST_RELEASE) && \
    wget -O /tmp/chromedriver.zip https://chromedriver.storage.googleapis.com/${CHROME_DRIVER_VERSION}/chromedriver_linux64.zip && \
    unzip /tmp/chromedriver.zip -d /usr/local/bin/ && \
    rm /tmp/chromedriver.zip && \
    chmod +x /usr/local/bin/chromedriver

COPY --from=builder /usr/app/target/app.jar /app.jar

EXPOSE 8082
ENTRYPOINT ["java", "-jar", "/app.jar"]
