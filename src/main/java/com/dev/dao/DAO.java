package com.dev.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// Interface générique DAO
public interface DAO<T> {
    Optional<T> findById(Integer id);
    List<T> findAll();
    T save(T entity);
    void update(T entity);
    void delete(Integer id);
}