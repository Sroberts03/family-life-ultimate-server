package com.app.chore.dto;

import java.util.Map;
import com.app.chore.types.Chore;

public record GetAllChoresRes(Map<Integer, Chore> chores) {

}
