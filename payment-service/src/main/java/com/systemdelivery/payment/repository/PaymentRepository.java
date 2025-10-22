package com.systemdelivery.payment.repository;

import com.systemdelivery.payment.model.Payment;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PaymentRepository extends MongoRepository<Payment, ObjectId> {
}
