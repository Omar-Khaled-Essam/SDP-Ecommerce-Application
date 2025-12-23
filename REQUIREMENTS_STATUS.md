E-Commerce Management System
The E-Commerce Management System is a desktop-based application designed to streamline the management of an online shopping platform. The system facilitates tasks such as product management, order tracking, and customer management, and provides role-based access for the Admin and Customer. The application allows secure registration, login, and management of e-commerce operations, including inventory, orders, and payments. ⚠️ (GUI exists, but many listed features are missing; auth is in-memory)
________________________________________
Modules and Features
For the Admin:
1. User Registration and Login

○ Description: The admin can log in securely with a unique username and password. ⚠️ (login exists; not “secure” in the academic/security sense—plain text in-memory)

○ Functionality: Includes password recovery and role-based access control. ❌ (no password recovery; role routing exists but no enforced access control/session layer)

2. Product Management

○ Description: The admin can add, edit, delete, and view products. ⚠️ (add/delete + list view exist; edit missing)

○ Functionality: Products include attributes such as name, category, price, stock quantity, and description. ⚠️ (model supports all; admin UI only inputs name/price/stock and hardcodes category/description)

3. Order Management

○ Description: The admin can view and update customer orders. ⚠️ (view/update exists for in-memory orders)

○ Functionality: Orders can be marked as “Pending,” “Shipped,” “Delivered,” or “Cancelled.” ✅ (supported and used in admin UI)

4. Customer Management

○ Description: The admin can view and manage registered customer accounts. ⚠️ (can view customer list/details; no edit/delete management actions)

○ Functionality: Access customer details, order history, and total purchase value. ✅ (shown in admin UI with order list + order-details popup)

5. Sales Reports

○ Description: The admin can generate reports summarizing total sales and revenue. ⚠️ (`Database.getTotalSales()` exists, but no GUI/report screen)

○ Functionality: Reports can be filtered by day, week, month, or custom date ranges. ❌ (no timestamps, filters, or reporting UI)

6. Inventory Tracking

○ Description: The admin can monitor stock levels and receive alerts for low-stock products. ❌ (no monitoring/alerts UI)

○ Functionality: Real-time stock updates upon order confirmation. ❌ (checkout does not reduce stock)
________________________________________
For the Customer:
1. User Registration and Login

○ Description: The customer can register and log in securely. ⚠️ (register/login exists; in-memory/plain text)

○ Functionality: Includes password recovery and account management. ❌ (no password recovery; no account management UI)

2. Browse Products

○ Description: Customers can view all available products by category. ⚠️ (can view products; no category browsing/filter UI)

○ Functionality: Includes product filtering and search by name, price, or category. ❌ (not implemented)

3. Shopping Cart

○ Description: Customers can add or remove products from their cart before checkout. ⚠️ (add exists; remove not exposed in UI)

○ Functionality: Displays total cost dynamically based on selected items. ⚠️ (total is calculated at checkout; not dynamically displayed)

4. Order Placement and Payment

○ Description: Customers can place orders and make payments through available payment methods. ⚠️ (order placement exists; payment methods not wired into UI)

○ Functionality: System calculates total cost (including tax and delivery fees). ❌ (no tax/delivery fee logic)

5. Order Tracking

○ Description: Customers can view the current status of their orders. ❌ (no tracking UI)

○ Functionality: Updates automatically as admin changes order status. ⚠️ (order notifies observer, but only prints to console; no UI update)

6. Purchase History

○ Description: Customers can view their previous orders. ❌ (no purchase history UI)

○ Functionality: Displays past purchases, total spent, and order details. ❌ (not implemented)
________________________________________
Deliverables
1. System with GUI

○ A functional application with a user-friendly graphical interface that fulfills all listed system requirements. ⚠️ (GUI exists, but does not fulfill all requirements yet)

2. Design Patterns

○ Implement at least 3 appropriate software design patterns to enhance maintainability and scalability (e.g., Singleton, Factory, Observer). ✅ (already present: Singleton/Factory/Observer + Strategy/Builder exist, though some are unused by UI)

3. UML Class Diagram

○ Provide a UML class diagram illustrating system architecture and the implemented design patterns. ❌ (not present in repo)