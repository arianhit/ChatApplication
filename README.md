# ChatApplication
The application is designed to allow multiple clients to communicate with each other through chat channels. This means that users can create new channels, subscribe to existing channels, and unsubscribe from channels at any time, allowing for maximum flexibility and convenience.

The application uses multi-threading to manage the connections between clients, ensuring that messages are transmitted quickly and efficiently. Each client is assigned a separate thread, allowing them to communicate with the server and other clients without interference or delays.

All messages sent through the application are securely stored in a text file, ensuring that they can be retrieved even if the server experiences an unexpected outage or other technical difficulties. This adds an extra layer of reliability to the application and ensures that important messages are not lost.

The application also includes a robust login system, with user credentials securely stored in a client database (ClientDB) for easy management and authentication. This helps to prevent unauthorized access and ensures that only authorized users can participate in the chat channels.

Overall, this multi-threaded application is designed to be efficient, reliable, and user-friendly, allowing multiple users to communicate with each other through chat channels in a secure and flexible environment.
