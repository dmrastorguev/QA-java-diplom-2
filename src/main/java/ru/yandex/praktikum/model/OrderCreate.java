package ru.yandex.praktikum.model;

import java.util.ArrayList;

public class OrderCreate {

    ArrayList<String>  ingredients;

    public OrderCreate(ArrayList<String> ingredients) {
        this.ingredients = ingredients;
    }

    public ArrayList<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(ArrayList<String> ingredients) {
        this.ingredients = ingredients;
    }

    @Override
    public String toString() {
        return "OrderCreate{" +
                "ingredients=" + ingredients +
                '}';
    }
}

