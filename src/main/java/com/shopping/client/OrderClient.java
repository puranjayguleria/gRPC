package com.shopping.client;

import java.util.List;
import java.util.logging.Logger;

import com.shopping.stubs.order.Order;
import com.shopping.stubs.order.OrderRequest;
import com.shopping.stubs.order.OrderResponse;
import com.shopping.stubs.order.OrderServiceGrpc;
import com.shopping.stubs.order.OrderServiceGrpc.OrderServiceBlockingStub;

import io.grpc.Channel;

// This client needs to be called by userServiceImpl then this client will call the orders Servide Impl and we will get orders of the users voila!!
public class OrderClient {

	private static final Logger logger = Logger.getLogger(OrderClient.class.getName());

	// get stub object
	// call service method

	//This stub is not for async calls but sync calls
	private OrderServiceBlockingStub orderServiceBlockingStub; // This will be used to make call to OrderServiceImpl class
	// Previously we were making the calls through BloomRPC

	public OrderClient(Channel channel) {

		orderServiceBlockingStub = OrderServiceGrpc.newBlockingStub(channel);
	}
	
	public List<Order> getOrders(int userId) {
		logger.info("Order Client calling the Order Service Method");
		OrderRequest orderRequest = OrderRequest.newBuilder().setUserId(userId).build();
		OrderResponse orderResponse = orderServiceBlockingStub.getOrdersForUser(orderRequest);
		return orderResponse.getOrderList();
	}

}
