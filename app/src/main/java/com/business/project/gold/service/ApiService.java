package com.business.project.gold.service;

import com.business.project.gold.domain.CouponCodeDetailsDto;
import com.business.project.gold.domain.CouponCodeRedeemResponse;
import com.business.project.gold.domain.CumulativeRevenueDetails;
import com.business.project.gold.domain.NewOrderRequest;
import com.business.project.gold.domain.OrderCancellationRequest;
import com.business.project.gold.domain.OrderDetailsWithUserDetailsDTO;
import com.business.project.gold.domain.UserDetails;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {
    @GET("/user/all")
    Call<List<UserDetails>> getUsers();

    @POST("/order/new")
    Call<OrderDetailsWithUserDetailsDTO> placeNewOrder(@Body NewOrderRequest request);

    @POST("/order/edit")
    Call<OrderDetailsWithUserDetailsDTO> editOrder(@Query(value="orderId") Long orderId, @Body NewOrderRequest request);

    @GET("/order")
    Call<OrderDetailsWithUserDetailsDTO> getAnOrder(@Query(value="orderId") Long orderId);

    @GET("/order/all")
    Call<List<OrderDetailsWithUserDetailsDTO>> getAllOrders();

    @POST("/order/cancel")
    Call<OrderDetailsWithUserDetailsDTO> cancelOrder(@Body OrderCancellationRequest request);

    @POST("/order/settle")
    Call<OrderDetailsWithUserDetailsDTO> settleOrder(@Query(value="orderId") Long orderId);

    @GET("/revenue/details/cumulative")
    Call<CumulativeRevenueDetails> getCumulativeRevenueDetails();

    @GET("/revenue/details/cumulative")
    Call<CumulativeRevenueDetails> getCumulativeRevenueDetailsForDateRange(@Query(value="startDate") Long startDate, @Query(value="endDate") Long endDate);

    @POST("/coupon-code/create")
    Call<Void> createCouponCode(@Body CouponCodeDetailsDto request);

    @GET("/coupon-code/redeem")
    Call<CouponCodeRedeemResponse> redeemCouponCode(@Query(value="couponCode") String couponCode);
}
