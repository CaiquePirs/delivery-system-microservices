package com.deliveysistem.notification.generator;

import com.deliveysistem.notification.event.representation.ItemsOrderEventDTO;
import com.deliveysistem.notification.model.Notification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import java.nio.charset.StandardCharsets;


@Component
@Slf4j
public class EmailTemplateGenerator {

    public String generate(Notification notification) throws Exception {
        ClassPathResource resource = new ClassPathResource("templates/email-order-confirmation.html");
        String html = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

        html = html
                .replace("{{confirmationPhrase}}", notification.getText())
                .replace("{{customer.name}}", notification.getBody().customer().name())
                .replace("{{customer.email}}", notification.getBody().customer().email())
                .replace("{{customer.phone}}", notification.getBody().customer().phone())
                .replace("{{customer.deliveryAddress.street}}", notification.getBody().customer().deliveryAddress().street())
                .replace("{{customer.deliveryAddress.number}}", notification.getBody().customer().deliveryAddress().number())
                .replace("{{customer.deliveryAddress.neighborhood}}", notification.getBody().customer().deliveryAddress().neighborhood())
                .replace("{{customer.deliveryAddress.city}}", notification.getBody().customer().deliveryAddress().city())
                .replace("{{customer.deliveryAddress.state}}", notification.getBody().customer().deliveryAddress().state())
                .replace("{{customer.deliveryAddress.zipcode}}", notification.getBody().customer().deliveryAddress().zipcode())
                .replace("{{customer.deliveryAddress.country}}", notification.getBody().customer().deliveryAddress().country())
                .replace("{{order.id}}", notification.getBody().id())
                .replace("{{order.orderDate}}", notification.getBody().orderDate().toString())
                .replace("{{order.status}}", notification.getBody().status())
                .replace("{{order.estimated_delivery}}", notification.getBody().estimated_delivery().toString())
                .replace("{{order.notes}}", notification.getBody().notes())
                .replace("{{order.total}}", notification.getBody().total().toString());

        StringBuilder itemsHtml = new StringBuilder();
        for (ItemsOrderEventDTO item : notification.getBody().items()) {
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

