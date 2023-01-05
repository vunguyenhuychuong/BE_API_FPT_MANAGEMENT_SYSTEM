package com.java8.tms.common.payload.request;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UploadCsvForm {
    String encodeType;
    String columnSeparator;
    String duplicateHandle;
    @Builder.Default
    List<String> scans = new ArrayList<>();
}


