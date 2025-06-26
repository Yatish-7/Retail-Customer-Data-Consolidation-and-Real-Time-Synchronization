Retail Customer Data Consolidation and Real‑Time Synchronization

🚀 About The Project
Retail Customer Data Consolidation and Real‑Time Synchronization is a robust Java‑based solution developed to streamline customer data handling across retail environments.
It aggregates data from point‑of‑sale (POS) systems and online registrations, synchronizing it in real‑time using RabbitMQ. The solution is built to:

Maintain data consistency across platforms

Minimize manual data entry

Enable event‑driven data flows across microservices

Enhance operational efficiency

Serve as a foundation for retail analytics and customer experience optimization

🛠️ Key Features
✅ Real‑Time Synchronization: Processes changes from multiple sources instantly.

✅ RabbitMQ Integration: Enables asynchronous, decoupled, and highly available data transfers.

✅ PostgreSQL Support: Provides robust database storage and access.

✅ Data Consolidation: Aggregates data from POS and online platforms.

✅ JSON Conversion: Enables seamless data interchange between services.

🏗️ Built With
Here are some of the major technologies that power this project:

Java 11 — The programming language for backend services

Maven — Build and dependency management

RabbitMQ — High‑performance message broker for asynchronous communication

PostgreSQL — Reliable relational database for consolidated data storage

MS SQL Server — Supported source database for extracting POS and online registration data

⚡️ Installation
Clone the Repository:

bash
Copy
Edit
git clone https://github.com/Yatish-7/Retail-Customer-Data-Consolidation-and-Real-Time-Synchronization.git
cd Retail-Customer-Data-Consolidation-and-Real-Time-Synchronization
Build the Services:

POSDataSync:

bash
Copy
Edit
mvn clean install
UnifiedCustomerSync:

bash
Copy
Edit
mvn clean install
Run the Services:

POSDataSync:

bash
Copy
Edit
java -jar POSDataSync/target/POSDataSync-1.0.jar
UnifiedCustomerSync:

bash
Copy
Edit
java -jar UnifiedCustomerSync/target/UnifiedCustomerSync-1.0.jar
🛠️ Usage
Point your POS or Registration source database connections in the AppConfig and DBConfig files.
Point your RabbitMQ connections in RabbitMQConfig.
Run both services and watch as data flows from source databases ➔ RabbitMQ ➔ Final consolidated database.

📞 Contact
Email: mailtoyatish55@gmail.com

📝 License
Distributed under the MIT License. See the full license for details.
