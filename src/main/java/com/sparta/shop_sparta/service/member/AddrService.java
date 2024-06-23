package com.sparta.shop_sparta.service.member;

public interface AddrService {
    void addAddr(Long memberId, String addr, String addrDetail);
    void removeAddr(Long addrId);
    void updateAddr(Long addrId, String addr, String addrDetail);
    void getAddr(Long addrId);
    void getAddrList(Long memberId);
}
