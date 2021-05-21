package com.sagar.springbootapidemo;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

@RestController
public class InventoryController {
    ArrayList<Item> inventory = new ArrayList<Item>(
            Arrays.asList(
                    new Item(1, "Keyboard", 29.99, 76),
                    new Item(2, "Mouse", 19.99, 43),
                    new Item(3, "Monitor", 79.99, 7),
                    new Item(4, "PC", 749.99, 2),
                    new Item(5, "Headphones", 19.99, 14)
            ));

    private Item findItem(Long id) {
        // Search the list of items for the item of interest.
        // If the item does not exist, return null;
        return inventory.stream()
                .filter(i -> id.equals(i.getId()))
                .findAny()
                .orElse(null);
    }

    @GetMapping("/items")
    public ArrayList<Item> getInventory() {
        // Spring Boot will handle the serialization of the ArrayList
        // to a JSON array, so we can return the list directly.
        return inventory;
    }

    @GetMapping("/items/{id}")
    public Item getItem(@PathVariable("id") Long id) {
        Item item = findItem(id);

        if (item == null) {
            // If the item does not exist, return 404
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Item Not Found");
        } else {
            return item;
        }

    }

    @PostMapping("/items")
    @ResponseStatus(HttpStatus.CREATED)
    public Item createItem(@RequestBody Item req) {
        // Since item IDs cannot be changed, generated a new ID, create a new item, add it to the
        // inventory, and then return the new item.
        Long newID = inventory.get(inventory.size() - 1).getId() + 1;
        Item newItem = new Item(newID, req.getName(), req.getPrice(), req.getCount());
        inventory.add(newItem);
        return newItem;
    }

    @PutMapping("/items/{id}")
    public Item updateItem(@PathVariable("id") Long id, @RequestBody Map<String, String> req) {
        Item item = findItem(id);

        if (item == null) {
            // If the item does not exist, return 404
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Item Not Found");
        } else {
            // Item exists, so update an existing one
            // Iterate over the POSTed JSON fields
            for (String k : req.keySet()) {
                // Update the items price if a price field is provided
                if (k.equals("price")) {
                    item.setPrice(Double.parseDouble(req.get(k)));
                    // Update the items count if a count field is provided
                } else if (k.equals("count")) {
                    item.setCount(Integer.parseInt(req.get(k)));
                }
            }

            // Return the updated item
            return item;
        }
    }

    @DeleteMapping("/items/{id}")
    public void deleteItem(@PathVariable("id") Long id) {
        Item item = findItem(id);

        if (item == null) {
            // If the item does not exist, return 404
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Item Not Found");
        } else {
            // If the item exists, delete it from the list
            inventory.remove(item);
        }
    }


}
