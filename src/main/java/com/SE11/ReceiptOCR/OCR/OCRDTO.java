// package com.SE11.ReceiptOCR.OCR;

// import com.SE11.ReceiptOCR.Receipt.ReceiptDTO;
// import com.SE11.ReceiptOCR.Expense.ExpenseDTO;

// public class OCRDTO {
//     private ReceiptDTO receiptDTO;
//     private ExpenseDTO expenseDTO;
//     private String base64Data;

//     // 기본 생성자
//     public OCRDTO() {}

//     // 매개변수를 받는 생성자
//     public OCRDTO(ReceiptDTO receiptDTO, ExpenseDTO expenseDTO, String base64Data) {
//         this.receiptDTO = receiptDTO;
//         this.expenseDTO = expenseDTO;
//         this.base64Data = base64Data;
//     }

//     // Getter 메소드
//     public ReceiptDTO getReceiptDTO() {
//         return receiptDTO;
//     }

//     public ExpenseDTO getExpenseDTO() {
//         return expenseDTO;
//     }

//     public String getBase64Data() {
//         return base64Data;
//     }

//     // Setter 메소드
//     public void setReceiptDTO(ReceiptDTO receiptDTO) {
//         this.receiptDTO = receiptDTO;
//     }

//     public void setExpenseDTO(ExpenseDTO expenseDTO) {
//         this.expenseDTO = expenseDTO;
//     }

//     public void setBase64Data(String base64Data) {
//         this.base64Data = base64Data;
//     }
// }
package com.SE11.ReceiptOCR.OCR;

import com.SE11.ReceiptOCR.Receipt.ReceiptDTO;
import com.SE11.ReceiptOCR.Expense.ExpenseDTO;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter

public class OCRDTO {
    private ReceiptDTO receiptDTO;
    private ExpenseDTO expenseDTO;
    private String base64Data;
}