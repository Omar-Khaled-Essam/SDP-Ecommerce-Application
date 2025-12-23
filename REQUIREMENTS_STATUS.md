Legend: ✅ implemented | ⚠️ partial/basic | ❌ missing | ❓ unclear/not shown

E-Commerce Management System
The E-Commerce Management System is a desktop-based application designed to streamline the management of an online shopping platform. The system facilitates tasks such as product management, order tracking, and customer management, and provides role-based access for the Admin and Customer. The application allows secure registration, login, and management of e-commerce operations, including inventory, orders, and payments. 
⚠️ (GUI exists; PBKDF2 hashing + SQLite added, but many listed features are still missing)
________________________________________
Modules and Features
For the Admin:
1. User Registration and Login

	 Description: The admin can log in securely with a unique username and password. 
⚠️ (login uses hashed passwords in SQLite; basic validation only) //no guarl rails

	 Functionality: Includes password recovery and role-based access control. 
⚠️ (math-challenge password reset added; role routing exists but no enforced access control/session layer)

2. Product Management

	 Description: The admin can add, edit, delete, and view products. 
⚠️ (add/edit/delete/view implemented; edits persisted to SQLite)

	 Functionality: Products include attributes such as name, category, price, stock quantity, and description. 
⚠️ (UI captures all fields; still basic validation/UX)

3. Order Management

	 Description: The admin can view and update customer orders. 
✅ (view/update persisted in SQLite; statuses applied)

	 Functionality: Orders can be marked as "Pending," "Shipped," "Delivered," or "Cancelled." 
✅ (supported and used in admin UI)

4. Customer Management

	 Description: The admin can view and manage registered customer accounts. 
⚠️ (can view customers, reset passwords, delete accounts; no username edit)

	 Functionality: Access customer details, order history, and total purchase value. 
⚠️ (shown in admin UI with order list + order-details popup)

5. Sales Reports

	 Description: The admin can generate reports summarizing total sales and revenue. 
✅ (sales report window present; totals shown)

	 Functionality: Reports can be filtered by day, week, month, or custom date ranges. 
⚠️ (date filters available via date pickers/quick ranges; based on stored timestamps; basic UI)

6. Inventory Tracking

	 Description: The admin can monitor stock levels and receive alerts for low-stock products. 
⚠️ (no alerts UI; stock stored in DB and reduced on checkout)

	 Functionality: Real-time stock updates upon order confirmation. 
⚠️ (stock reduced on checkout; no low-stock warnings)
________________________________________
For the Customer:
1. User Registration and Login

	 Description: The customer can register and log in securely. 
⚠️ (register/login uses hashed passwords in SQLite; minimal validation)

	 Functionality: Includes password recovery and account management. 
⚠️ (math-challenge password reset available; no account management UI)

2. Browse Products

	 Description: Customers can view all available products by category. 
✅ (can view products; category choice/filter UI present)

	 Functionality: Includes product filtering and search by name, price, or category. 
✅ (filters/search by name, price range, category implemented)

3. Shopping Cart

	 Description: Customers can add or remove products from their cart before checkout. 
⚠️ (add/remove exposed in UI)

	 Functionality: Displays total cost dynamically based on selected items. 
⚠️ (total label updates on cart changes; tax/delivery added at checkout)

4. Order Placement and Payment

	 Description: Customers can place orders and make payments through available payment methods. 
⚠️ (order placement exists; payment method choice (Credit Card/PayPal) mocked in UI)

	 Functionality: System calculates total cost (including tax and delivery fees). 
⚠️ (tax 14% + delivery fee applied at checkout)

5. Order Tracking

	 Description: Customers can view the current status of their orders. 
❌ (no tracking UI) //should reflect the current status of the checkout 

	 Functionality: Updates automatically as admin changes order status. 
⚠️ (notifying logic exists; no UI update)

6. Purchase History

	 Description: Customers can view their previous orders. 
❌ (no purchase history UI)

	 Functionality: Displays past purchases, total spent, and order details. 
❌ (not implemented)
________________________________________
Deliverables
1. System with GUI

	 A functional application with a user-friendly graphical interface that fulfills all listed system requirements. 
⚠️ (GUI exists, but does not fulfill all requirements yet)

2. Design Patterns

	 Implement at least 3 appropriate software design patterns to enhance maintainability and scalability (e.g., Singleton, Factory). 
✅ (present: Singleton, Factory, Builder, Adapter; other patterns optional)

3. UML Class Diagram

	 Provide a UML class diagram illustrating system architecture and the implemented design patterns. 
❌ (not present in repo)

Tooling note:
- VS Code Run (Gradle): Added .vscode/tasks.json with Gradle: Run App (./gradlew run) and .vscode/launch.json that invokes the task. .vscode/settings.json remains gitignored due to hardcoded paths.
