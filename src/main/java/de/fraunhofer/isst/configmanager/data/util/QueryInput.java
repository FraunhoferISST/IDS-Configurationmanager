package de.fraunhofer.isst.configmanager.data.util;

import lombok.Data;

import java.util.HashMap;

@Data
public class QueryInput {
    HashMap<String, String> headers = new HashMap<>();
    HashMap<String, String> params = new HashMap<>();
    HashMap<String, String> pathVariables = new HashMap<>();
}