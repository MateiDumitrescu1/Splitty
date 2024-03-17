package server.api;

import commons.Expense;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;
import server.database.ExpenseRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class TestExpenseRepository implements ExpenseRepository {

    public final List<Expense> expenses = new ArrayList<>();
    public final List<String> calledMethods = new ArrayList<>();

    private void call(String name) {
        calledMethods.add(name);
    }

    @Override
    public void flush() {

    }

    @Override
    public <S extends Expense> S saveAndFlush(S entity) {
        return null;
    }

    @Override
    public <S extends Expense> List<S> saveAllAndFlush(Iterable<S> entities) {
        return null;
    }

    @Override
    public void deleteAllInBatch(Iterable<Expense> entities) {

    }

    @Override
    public void deleteAllByIdInBatch(Iterable<Long> longs) {

    }

    @Override
    public void deleteAllInBatch() {

    }

    @Override
    public Expense getOne(Long aLong) {
        return null;
    }

    @Override
    public Expense getById(Long aLong) {
        return null;
    }

    @Override
    public Expense getReferenceById(Long aLong) {
        return null;
    }

    @Override
    public <S extends Expense> Optional<S> findOne(Example<S> example) {
        return Optional.empty();
    }

    @Override
    public <S extends Expense> List<S> findAll(Example<S> example) {
        return null;
    }

    @Override
    public <S extends Expense> List<S> findAll(Example<S> example, Sort sort) {
        return null;
    }

    @Override
    public <S extends Expense> Page<S> findAll(Example<S> example, Pageable pageable) {
        return null;
    }

    @Override
    public <S extends Expense> long count(Example<S> example) {
        return 0;
    }

    @Override
    public <S extends Expense> boolean exists(Example<S> example) {
        return false;
    }

    @Override
    public <S extends Expense, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        return null;
    }

    @Override
    public <S extends Expense> S save(S entity) {
        return null;
    }

    @Override
    public <S extends Expense> List<S> saveAll(Iterable<S> entities) {
        return null;
    }

    @Override
    public Optional<Expense> findById(Long aLong) {
        return Optional.empty();
    }

    @Override
    public boolean existsById(Long aLong) {
        return false;
    }

    @Override
    public List<Expense> findAll() {
        return null;
    }

    @Override
    public List<Expense> findAllById(Iterable<Long> longs) {
        return null;
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public void deleteById(Long aLong) {

    }

    @Override
    public void delete(Expense entity) {

    }

    @Override
    public void deleteAllById(Iterable<? extends Long> longs) {

    }

    @Override
    public void deleteAll(Iterable<? extends Expense> entities) {

    }

    @Override
    public void deleteAll() {

    }

    @Override
    public List<Expense> findAll(Sort sort) {
        return null;
    }

    @Override
    public Page<Expense> findAll(Pageable pageable) {
        return null;
    }
}