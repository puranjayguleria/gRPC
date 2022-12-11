package com.shopping.service;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.google.protobuf.util.Timestamps;
import com.shopping.db.Order;
import com.shopping.db.OrderDao;
import com.shopping.stubs.order.OrderRequest;
import com.shopping.stubs.order.OrderResponse;
import com.shopping.stubs.order.OrderServiceGrpc.OrderServiceImplBase;

import io.grpc.stub.StreamObserver;

public class OrderServiceImpl extends OrderServiceImplBase{

	private static final Logger logger = Logger.getLogger(OrderServiceImpl.class.getName());

	private OrderDao orderDao = new OrderDao();
	
	@Override
	public void getOrdersForUser(OrderRequest request, StreamObserver<OrderResponse> responseObserver) {
		List<Order> orders = orderDao.getOrders(request.getUserId());
		
		logger.info("Got  Orders from OrderDao and converting to Order Response proto object");
		List<com.shopping.stubs.order.Order> ordersForUser = orders.stream().map(order -> com.shopping.stubs.order.Order.newBuilder()
		.setUserId(order.getUserId())
		.setNoOfItems(order.getNoOfItems())
		.setOrderDate(Timestamps.fromMillis(order.getOrderDate().getTime()))
		.setTotalAmount(order.getTotalAmount())
		.setOrderId(order.getOrderId()).build()).collect(Collectors.toList());
		
		OrderResponse orderResponse = OrderResponse.newBuilder().addAllOrder(ordersForUser).build();
		
		responseObserver.onNext(orderResponse);
		responseObserver.onCompleted();
		
	}
	
	

}
