#
# Copyright © 2018-2019 Hashmap, Inc
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

## STEP 1
FROM openjdk:11-jdk-slim as build

WORKDIR /workspace/app

COPY df-server df-server
COPY df-utils df-utils
COPY df-valve df-valve
COPY mvnw  mvnw
COPY .mvn .mvn
COPY pom.xml pom.xml

RUN ./mvnw clean package


## STEP 2
FROM openjdk:11-jdk-slim

COPY --from=build /workspace/app/df-server/target/*.jar /app/app.jar

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "/app/app.jar"]