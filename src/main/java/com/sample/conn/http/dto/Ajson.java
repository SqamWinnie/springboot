package com.sample.conn.http.dto;

import lombok.Data;

import java.util.List;

/**
 * @author winnie
 * @date 2020/10/28
 */
@Data
public class Ajson {
    private List<Person> persons;
    private List<Car> cars;
}
