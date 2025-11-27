package com.kosi.financetracker.backend.service;

import com.kosi.financetracker.backend.model.Transaction;   // ✅ correct entity import
import com.kosi.financetracker.backend.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    public List<Transaction> getAllTransactions(Long userId) {
        return transactionRepository.findByUserId(userId);
    }

    public Transaction createTransaction(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    public Transaction updateTransaction(Long id, Transaction transaction) {
        Transaction existing = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
        // Or ResourceNotFoundException if you have it

        existing.setName(transaction.getName());
        existing.setCategory(transaction.getCategory());
        existing.setAmount(transaction.getAmount());
        // Update other fields if needed

        return transactionRepository.save(existing);
    }

    public void deleteTransaction(Long id) {
        transactionRepository.deleteById(id);
    }
}
