# Stock exchange

This project was created for the Concurent and Event-Based Programming laboratory.

## Functionalities

- Clients can be both buyers and sellers.
- Buyers have money and place offers, where they show their intention to buy a certain number of stocks for a price.
- Sellers have stocks and they place offers, where they show the number of stocks that they are selling and for what price.
- The system matches offers and creates a transaction.

## Entities
  
### Client
- id
- name
- clientCount
- transactionHistory
- moneyWallet
- stockWallet

### Offer
- id
- nameOfStock
- noOfStock
- pricePerStock
- clientID
- type
- isCompleted

### Transaction
- id
- sellerID
- buyerID
- sellingOfferID
- buyingOfferID
- nameOfTradedStock
- noOfTradedStocks
- priceOfTradedStocks
- totalPrice

### Matcher

## Diagram
![image](https://github.com/user-attachments/assets/7289ddf2-38ed-4eeb-ada0-4208c409e8a0)
