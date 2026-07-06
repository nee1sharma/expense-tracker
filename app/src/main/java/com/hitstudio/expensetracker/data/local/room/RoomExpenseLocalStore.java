package com.hitstudio.expensetracker.data.local.room;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.hitstudio.expensetracker.data.local.ExpenseLocalReader;
import com.hitstudio.expensetracker.data.local.ExpenseLocalWriter;
import com.hitstudio.expensetracker.domain.model.DateRange;
import com.hitstudio.expensetracker.domain.model.Expense;

import java.util.List;

public class RoomExpenseLocalStore implements ExpenseLocalReader, ExpenseLocalWriter {
    private final ExpenseDao dao;

    public RoomExpenseLocalStore(ExpenseDao dao) {
        this.dao = dao;
    }

    @Override
    public LiveData<List<Expense>> observeInRange(DateRange range) {
        return Transformations.map(dao.observeInRange(range.startMillis, range.endMillis), ExpenseEntityMapper::fromRecords);
    }

    @Override
    public LiveData<List<Expense>> observeRecent(int limit) {
        return Transformations.map(dao.observeRecent(limit), ExpenseEntityMapper::fromRecords);
    }

    @Override
    public LiveData<List<Expense>> observeAll() {
        return Transformations.map(dao.observeAll(), ExpenseEntityMapper::fromRecords);
    }

    @Override
    public List<Expense> getInRange(DateRange range) {
        return ExpenseEntityMapper.fromRecords(dao.getInRange(range.startMillis, range.endMillis));
    }

    @Override
    public List<Expense> getAll() {
        return ExpenseEntityMapper.fromRecords(dao.getAll());
    }

    @Override
    public Expense getById(long id) {
        return ExpenseEntityMapper.fromRecord(dao.getById(id));
    }

    @Override
    public int count() {
        return dao.count();
    }

    @Override
    public long insert(Expense expense) {
        long now = System.currentTimeMillis();
        if (expense.createdAt <= 0) {
            expense.createdAt = now;
        }
        expense.updatedAt = now;
        return dao.insert(ExpenseEntityMapper.toEntity(expense));
    }

    @Override
    public void update(Expense expense) {
        expense.updatedAt = System.currentTimeMillis();
        dao.update(ExpenseEntityMapper.toEntity(expense));
    }

    @Override
    public void delete(long id) {
        dao.softDelete(id, System.currentTimeMillis());
    }

    @Override
    public void insertAll(List<Expense> expenses) {
        dao.insertAll(ExpenseEntityMapper.toEntities(expenses));
    }

    @Override
    public void clearAll() {
        dao.clearAll();
    }
}
