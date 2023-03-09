/*
 * Copyright (C) 2021 The Android Open Source Project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.inventory

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.inventory.data.Item
import com.example.inventory.data.ItemDao
import kotlinx.coroutines.launch
import java.util.*

/**
 * View Model to keep a reference to the Inventory repository and an up-to-date list of all items.
 *
 */
class InventoryViewModel(private val itemDao: ItemDao) : ViewModel() {

    // Cache all items form the database using LiveData.
    val allItems: LiveData<List<Item>> = itemDao.getItems().asLiveData()

    /**
     * Returns true if stock is available to sell, false otherwise.
     */
//    fun isStockAvailable(item: Item): Boolean {
//        return (item.quantityInStock > 0)
//    }

    /**
     * Updates an existing Item in the database.
     */
    fun updateItem(
        itemId: Int,
        itemName: String,
        itemPrice: String,
        itemCount: String,
        imageUri: String,
        itemSum: String,
    ) {
        val updatedItem =
            getUpdatedItemEntry(itemId, itemName, itemPrice, itemCount, imageUri, itemSum)
        updateItem(updatedItem)
    }


    /**
     * Launching a new coroutine to update an item in a non-blocking way
     */
    private fun updateItem(item: Item) {
        viewModelScope.launch {
            itemDao.update(item)
        }
    }

    /**
     * Decreases the stock by one unit and updates the database.
     */
//    fun sellItem(item: Item) {
//        if (item.quantityInStock > 0) {
//            // Decrease the quantity by 1
//            val newItem = item.copy(quantityInStock = item.quantityInStock - 1)
//            updateItem(newItem)
//        }
//    }

    /**
     * Inserts the new Item into database.
     */
    fun addNewItem(
        itemName: String,
        itemPrice: String,
        itemCount: String,
        imageUri: String,
        itemSum: String
    ) {
        val newItem = getNewItemEntry(itemName, itemPrice, itemCount, imageUri, itemSum)
        insertItem(newItem)
    }

    /**
     * Launching a new coroutine to insert an item in a non-blocking way
     */
    private fun insertItem(item: Item) {
        viewModelScope.launch {
            itemDao.insert(item)
        }
    }

    /**
     * Launching a new coroutine to delete an item in a non-blocking way
     */
    fun deleteItem(item: Item) {
        viewModelScope.launch {
            itemDao.delete(item)
        }
    }

    /**
     * Retrieve an item from the repository.
     */
    fun retrieveItem(id: Int): LiveData<Item> {
        return itemDao.getItem(id).asLiveData()
    }

    /**
     * Returns true if the EditTexts are not empty
     */
    fun isEntryValid(
        itemName: String,
        itemPrice: String,
        itemCount: String,
        itemSum: String
    ): Boolean {
        if (itemName.isBlank() || itemPrice.isBlank() || itemCount.isBlank() || itemSum.isBlank()) {
            return false
        }
        return true
    }

    /**
     * Returns an instance of the [Item] entity class with the item info entered by the user.
     * This will be used to add a new entry to the Inventory database.
     */
    private fun getNewItemEntry(
        itemName: String,
        itemPrice: String,
        itemCount: String,
        imageUri: String,
        itemSum: String
    ): Item {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DATE, 5)

        return Item(
            itemName = itemName,
            itemPrice = itemPrice,
            quantityInStock = itemCount,
            expiryDate = calendar.time.time,
            imageUri = imageUri,
            itemSum = itemSum
        )
    }

    /**
     * Called to update an existing entry in the Inventory database.
     * Returns an instance of the [Item] entity class with the item info updated by the user.
     */
    private fun getUpdatedItemEntry(
        itemId: Int,
        itemName: String,
        itemPrice: String,
        itemCount: String,
        imageUri: String,
        itemSum: String,
    ): Item {

        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DATE, 5)

        return Item(
            id = itemId,
            itemName = itemName,
            expiryDate = calendar.time.time,
            imageUri = imageUri,
            itemPrice = itemPrice,
            quantityInStock = itemCount,
            itemSum = itemSum
        )
    }
}

/**
 * Factory class to instantiate the [ViewModel] instance.
 */
class InventoryViewModelFactory(private val itemDao: ItemDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(InventoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return InventoryViewModel(itemDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

