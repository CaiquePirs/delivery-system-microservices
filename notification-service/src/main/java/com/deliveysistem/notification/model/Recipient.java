package com.deliveysistem.notification.model;

import com.deliveysistem.notification.model.enums.RecipientType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Recipient {
    private String email;
    private RecipientType type;
}
