package com.business.project.gold.service;

public interface OrderActionListener {
    void onEditOrder(Long orderId);
    void onCancelOrder(Long orderId);
    void onSettleOrder(Long orderId);
}
