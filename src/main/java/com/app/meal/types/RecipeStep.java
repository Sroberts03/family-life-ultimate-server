package com.app.meal.types;

public class RecipeStep {
    int id;
    String instruction;
    int stepOrder;

    public RecipeStep(int id, String instruction, int stepOrder) {
        this.id = id;
        this.instruction = instruction;
        this.stepOrder = stepOrder;
    }
    
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getInstruction() {
        return instruction;
    }
    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }
    public int getStepOrder() {
        return stepOrder;
    }
    public void setStepOrder(int stepOrder) {
        this.stepOrder = stepOrder;
    }
}
