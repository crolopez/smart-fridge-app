[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)

# Smart Fridge

The purpose of this food inventory management system is to facilitate knowledge of what products are stored in the refrigerator or pantry from anywhere and at any time, allowing to manage self-generated shopping lists and to launch notifications reminding the user of the scarcity or caducity of certain products when passing near establishments where they can be purchased.

This repository includes the system frontend, which has been designed to be intuitive and easy to use by all kinds of people with an Android smartphone. You can find the frontend of the project in [this repository](https://github.com/crolopez/smart-fridge).

## How to use

To use the application you must import this project from Android Studio and build the binary, which can be installed on devices starting with Android 5.0.

Once the application is open, you will be able to navigate between the different menu tabs. These are:

- **Home**: This tab includes the presentation of the application, as well as the date of last synchronization and a button to force it.
- **Inventory**: Allows you to see the food inventory that has been synchronized, as well as relevant information for each product such as quantity, allergens, additives... If you click on a product, it is added to the shopping list.
- **List**: Allows you to write a shopping list moving products from the previous tab, adding new ones, or pressing the autogeneration button. It is also possible to indicate the preferred place to purchase each product.
- **Map**: The purpose of this tab is to integrate the system with the geolocation of the user so that you can configure alerts if the user is close to a store where he has configured part of the shopping list.
- **Settings**: Allows the user to configure aspects of the application such as the type of map display, the alert threshold, or the address and port of the backend.
