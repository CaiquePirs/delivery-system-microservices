package com.deliveysistem.notification.generator;

import com.deliveysistem.notification.event.representation.ItemsOrderEventDTO;
import com.deliveysistem.notification.model.Notification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;


@Component
@Slf4j
public class EmailTemplateGenerator {

    public String generate(Notification notification) throws Exception {
        ClassPathResource resource = new ClassPathResource("templates/email-order-confirmation.html");
        String html = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

        html = html
                .replace("{{confirmationPhrase}}", notification.getText())
                .replace("{{customer.name}}", notification.getBody().getCustomer().name())
                .replace("{{customer.email}}", notification.getBody().getCustomer().email())
                .replace("{{customer.phone}}", notification.getBody().getCustomer().phone())
                .replace("{{customer.deliveryAddress.street}}", notification.getBody().getCustomer().deliveryAddress().street())
                .replace("{{customer.deliveryAddress.number}}", notification.getBody().getCustomer().deliveryAddress().number())
                .replace("{{customer.deliveryAddress.neighborhood}}", notification.getBody().getCustomer().deliveryAddress().neighborhood())
                .replace("{{customer.deliveryAddress.city}}", notification.getBody().getCustomer().deliveryAddress().city())
                .replace("{{customer.deliveryAddress.state}}", notification.getBody().getCustomer().deliveryAddress().state())
                .replace("{{customer.deliveryAddress.zipcode}}", notification.getBody().getCustomer().deliveryAddress().zipcode())
                .replace("{{customer.deliveryAddress.country}}", notification.getBody().getCustomer().deliveryAddress().country())
                .replace("{{order.id}}", notification.getBody().getId())
                .replace("{{order.orderDate}}", notification.getBody().getOrderDate().toString())
                .replace("{{order.status}}", notification.getBody().getStatus())
                .replace("{{order.estimated_delivery}}", notification.getBody().getEstimated_delivery().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")))
                .replace("{{order.notes}}", notification.getBody().getNotes())
                .replace("{{order.total}}", notification.getBody().getTotal().toString());

        StringBuilder itemsHtml = new StringBuilder();
        for (ItemsOrderEventDTO item : notification.getBody().getItems()) {
            itemsHtml.append("<tr>")
                    .append("<td>").append(item.id()).append("</td>")
                    .append("<td>").append(item.quantity()).append("</td>")
                    .append("<td>$").append(item.total()).append("</td>")
                    .append("<td>").append(item.menuId()).append("</td>")
                    .append("</tr>");
        }
        html = html.replace("{{#each order.items}}", "")
                .replace("{{/each}}", "")
                .replace("{{items}}", itemsHtml.toString());

        return html;
    }
}

