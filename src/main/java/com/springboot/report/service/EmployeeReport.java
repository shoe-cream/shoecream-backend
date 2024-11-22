package com.springboot.report.service;

import com.springboot.buyer_item.entity.BuyerItem;
import com.springboot.manufacture_item.entity.ItemManufacture;
import com.springboot.manufacture_item.repository.MfItemQueryRepositoryCustom;
import com.springboot.member.entity.Member;
import com.springboot.member.repository.MemberRepository;
import com.springboot.order_header.repository.OrderQueryRepositoryCustom;
import com.springboot.order_item.entity.OrderItems;
import com.springboot.order_item.repository.OrderItemQueryRepositoryCustom;
import com.springboot.report.reportDto.ReportDto;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class EmployeeReport {

    private final OrderItemQueryRepositoryCustom orderItemsRepository;
    private final OrderQueryRepositoryCustom orderRepository;
    private final MemberRepository memberRepository;
    private final MfItemQueryRepositoryCustom mfItemRepository;

    public EmployeeReport(OrderItemQueryRepositoryCustom orderItemsRepository, OrderQueryRepositoryCustom orderRepository, MemberRepository memberRepository, MfItemQueryRepositoryCustom mfItemRepository) {
        this.orderItemsRepository = orderItemsRepository;
        this.orderRepository = orderRepository;
        this.memberRepository = memberRepository;
        this.mfItemRepository = mfItemRepository;
    }


    public List<ReportDto.EmployeeReportDto> getEmployeesReport (LocalDate startDate, LocalDate endDate) {

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        // 전체 사원 목록을 가져옴
        List<Member> employees = memberRepository.findAll();

        // 전체 사원에 대해 실적을 계산
        List<ReportDto.EmployeeReportDto> reportDtos = employees.stream().map(employee -> {
            String employeeId = employee.getEmployeeId();

            // 해당 사원의 기간별 주문 정보 가져오기
            List<OrderItems> ordersInRange = orderItemsRepository.findByOrderHeadersRequestDateBetween(employeeId, startDateTime, endDateTime);

            // 실적 정보 계산
            ReportDto.EmployeeReportDto reportDto = new ReportDto.EmployeeReportDto();
            reportDto.setEmployeeId(employeeId);
            reportDto.setEmployeeName(employee.getName()); // 사원 이름 추가 가능
            reportDto.setTotalOrderCount(calculateOrderCountByEmployee(employeeId, startDateTime, endDateTime)); // 판매 건수
            reportDto.setTotalOrderPrice(calculateOrderPriceByEmployee(employeeId, startDateTime, endDateTime)); // 판매 금액
            reportDto.setMarginRate(calculateMarginByEmployee(employeeId, startDateTime, endDateTime)); // 마진률

            return reportDto;
        }).collect(Collectors.toList());

        return reportDtos;
    }

    public ReportDto.EmployeeReportDto getEmployeeReport (String employeeId, LocalDate startDate, LocalDate endDate) {

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        // 해당 사원의 기간별 주문 정보 가져오기
        List<OrderItems> ordersInRange = orderItemsRepository.findByOrderHeadersRequestDateBetween(employeeId, startDateTime, endDateTime);

        // 실적 정보 계산
        ReportDto.EmployeeReportDto reportDto = new ReportDto.EmployeeReportDto();
        reportDto.setEmployeeId(employeeId);
        reportDto.setTotalOrderCount(calculateOrderCountByEmployee(employeeId, startDateTime, endDateTime)); // 판매 건수
        reportDto.setTotalOrderPrice(calculateOrderPriceByEmployee(employeeId, startDateTime, endDateTime)); // 판매 금액
        reportDto.setMarginRate(calculateMarginByEmployee(employeeId, startDateTime, endDateTime)); // 마진률

        return  reportDto;
    }


    // 사원 실적 - 마진률 =  판매단가 / 제조단가
    public BigDecimal calculateMarginByEmployee (String employeeId, LocalDateTime start, LocalDateTime end) {

        // 영업사원이 판매한 제품 리스트
        List<OrderItems> orderItems = orderItemsRepository.findByOrderHeadersRequestDateBetween(employeeId, start, end);

        // 판매된 제품의 제조단가 리스트
        List<ItemManufacture> mfItems = mfItemRepository.findManufacturedItemsForOrderItems(employeeId, start, end);

        BigDecimal totalOrderPrice = BigDecimal.ZERO;
        BigDecimal totalManufacturePrice = BigDecimal.ZERO;

        //총 판매 가격 계산
        for (OrderItems orderItem : orderItems) {
            totalOrderPrice = totalOrderPrice.add(orderItem.getUnitPrice().multiply(BigDecimal.valueOf(orderItem.getQty())));
        }

        // 총 제조 가격 계산
        for (ItemManufacture mfItem : mfItems) {
            Optional<OrderItems> correspondingOrderItem = orderItems.stream()
                    .filter(orderItem -> orderItem.getItemCd().equals(mfItem.getItem().getItemCd())) // ItemCd가 일치하는지 확인
                    .findFirst();

            if (correspondingOrderItem.isPresent()) {
                OrderItems orderItem = correspondingOrderItem.get();
                // 해당 판매 수량 가져오기
                totalManufacturePrice = totalManufacturePrice.add(mfItem.getUnitPrice().multiply(BigDecimal.valueOf(orderItem.getQty())));
            }
        }


        // 나누기 0 조심
        if (totalManufacturePrice.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        //마진률 계산
        BigDecimal marginRate = (totalOrderPrice.subtract(totalManufacturePrice))
                .divide(totalOrderPrice, 2, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));

        return marginRate;
    }

    //사원 실적 - 판매 건수
    public Integer calculateOrderCountByEmployee (String employeeId, LocalDateTime start, LocalDateTime end) {

        Integer result = orderRepository.getOrderCountByEmployee(employeeId, start, end);

        return result != null ? result : 0;
    }

    //사원 실적 - 판매 금액 계산
    public BigDecimal calculateOrderPriceByEmployee (String employeeId, LocalDateTime start, LocalDateTime end) {
        BigDecimal result = orderItemsRepository.findTotalOrderPriceByItemCdAndOrderDateBetween(null, employeeId, start, end);

        return result != null ? result : BigDecimal.ZERO;
    }
}
