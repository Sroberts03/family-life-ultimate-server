package com.app.meal.dto;

import java.util.Map;
import com.app.meal.types.ShoppingListItem;

public record GetShoppingItemsRes(Map<Integer, ShoppingListItem> shoppingItems) {
    
}
