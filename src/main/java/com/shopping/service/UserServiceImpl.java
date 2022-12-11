package com.shopping.service;

import java.util.List;
import java.util.concurrent.TimeUnit;

import com.shopping.client.OrderClient;
import com.shopping.db.User;
import com.shopping.db.UserDAO;
import com.shopping.stubs.order.Order;
import com.shopping.stubs.user.Gender;
import com.shopping.stubs.user.UserRequest;
import com.shopping.stubs.user.UserResponse;
import com.shopping.stubs.user.UserServiceGrpc.UserServiceImplBase;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

public class UserServiceImpl extends UserServiceImplBase {

	UserDAO userDao = new UserDAO();

	@Override
	public void getUserDetails(UserRequest request, StreamObserver<UserResponse> responseObserver) {
		// this came from proto stub created by proto file

		User user = userDao.getDetails(request.getUsername());
		// Now Transform User object from DB to Response object defined in Proto
		// We will use Builders for this given by proto

		UserResponse.Builder userResponseBuilder = UserResponse.newBuilder().setId(user.getId()).setAge(user.getAge())
				.setGender(Gender.valueOf(user.getGender())) // as in proto gender is an enum
				.setName(user.getName()).setUsername(user.getUsername());

		/*
		 * Get orders for the user using order client
		 */

		List<Order> orders = getOrdersWithChannel(user);
		userResponseBuilder.setNoOfOrders(orders.size());
		UserResponse userResponse = userResponseBuilder.build();

		responseObserver.onNext(userResponse); // method used to return data back to client
		responseObserver.onCompleted();
	}

	private List<Order> getOrdersWithChannel(User user) {
		ManagedChannel channel = ManagedChannelBuilder.forTarget("localhost:50052").usePlaintext().build(); // This
																											// channel
																											// will help
																											// us to
																											// make a
																											// http2
																											// connection
																											// and grab
																											// the stub
																											// object in
																											// order
																											// client
		// See client constructor for more info
		OrderClient orderClient = new OrderClient(channel);
		List<Order> orders = orderClient.getOrders(user.getId());

		try {
			channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
		} catch (InterruptedException e) {

			e.printStackTrace();
		}
		return orders;
	}

}
