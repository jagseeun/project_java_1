package program;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class P_to_J {
    private List<String> questions = new ArrayList<>();
    private String userName = "";
    private int[] answers;

    public P_to_J() {
        loadQuestions();
        showIntroduction();
    }

    // 질문 로드
    private void loadQuestions() {
        try {
            questions = Files.readAllLines(Paths.get("questions.txt"));
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "질문 파일을 찾을 수 없습니다. 프로그램을 종료합니다.");
            System.exit(0);
        }
        answers = new int[questions.size()];
    }

    // 소개 화면
    private void showIntroduction() {
        showMessage("P to J", "<html>환영합니다!<br>이 프로그램은 성향 검사와 목표 관리 기능을 제공합니다.<br>시작하려면 확인 버튼을 누르세요.</html>", 
            "확인", e -> askUserName());
    }

    // 사용자 이름 입력
    private void askUserName() {
        JFrame frame = new JFrame("사용자 이름 입력");
        frame.setLayout(new FlowLayout());
        frame.setSize(400, 200);

        JLabel label = new JLabel("이름을 입력하세요:");
        JTextField textField = new JTextField(15);
        JButton button = new JButton("확인");

        button.addActionListener(e -> {
            userName = textField.getText().trim();
            if (userName.isEmpty()) userName = "익명";
            saveToFile("usernames.txt", userName);
            frame.dispose();
            startTest();
        });

        frame.add(label);
        frame.add(textField);
        frame.add(button);
        frame.setVisible(true);
    }

    // 테스트 시작
    private void startTest() {
        for (int i = 0; i < questions.size(); i++) {
            String[] options = {"1: 매우 그렇다", "2: 그렇다", "3: 보통이다", "4: 그렇지 않다", "5: 매우 그렇지 않다"};
            int answer = JOptionPane.showOptionDialog(null, 
                "Q" + (i + 1) + ": " + questions.get(i), 
                "테스트 진행", 
                JOptionPane.DEFAULT_OPTION, 
                JOptionPane.QUESTION_MESSAGE, 
                null, options, options[2]);

            if (answer == -1) {
                JOptionPane.showMessageDialog(null, "테스트가 중단되었습니다.");
                return;
            }
            answers[i] = answer + 1;
        }
        showFeedback();
    }

 // 공통 Frame 생성 메서드
    private JFrame createFrame(String title) {
        JFrame frame = new JFrame(title);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 600);
        frame.setLayout(new BorderLayout());
        frame.setLocationRelativeTo(null); // 화면 중앙에 배치
        return frame;
    }

    private void showFeedback() {
        JFrame frame = createFrame("테스트 결과");

        // 점수 계산
        int pScore = 0, jScore = 0;
        for (int i = 0; i < answers.length; i++) {
            if (i % 2 == 0) pScore += answers[i];
            else jScore += answers[i];
        }

        // 유형 판별
        String type = (pScore > jScore) ? "P" : (pScore < jScore) ? "J" : "중간";
        String feedbackPrefix = type.equals("P") ? "p_feedback" : type.equals("J") ? "j_feedback" : "m_feedback";
        int[] feedbackIndex = {0};

        // 결과 요약
        String resultSummary = String.format(
            "%s님의 검사 결과\n\nP 성향 점수: %d\nJ 성향 점수: %d\n최종 유형: %s형\n\n",
            userName, pScore, jScore, type
        );

        // 텍스트 영역
        JTextArea textArea = new JTextArea(resultSummary);
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(textArea);

        // 버튼
        JButton nextButton = new JButton("다음 피드백 보기");
        nextButton.addActionListener(e -> {
            if (!loadFeedback(feedbackPrefix, feedbackIndex[0], textArea, resultSummary)) {
                nextButton.setText("메인 메뉴로");
                nextButton.addActionListener(evt -> {
                    frame.dispose();
                    showMainMenu();
                });
            } else {
                feedbackIndex[0]++;
            }
        });

        // 첫 번째 피드백 로드
        loadFeedback(feedbackPrefix, feedbackIndex[0], textArea, resultSummary);

        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(nextButton, BorderLayout.SOUTH);
        frame.setVisible(true);
    }

    // 피드백 파일 읽기
    private boolean loadFeedback(String prefix, int index, JTextArea textArea, String resultSummary) {
        String fileName = prefix + (index == 0 ? "" : index) + ".txt"; // 파일 이름 설정
        try {
            List<String> lines = Files.readAllLines(Paths.get(fileName));
            StringBuilder feedbackContent = new StringBuilder(resultSummary);
            feedbackContent.append("\n").append("피드백 ").append(index + 1).append("\n");
            for (String line : lines) {
                feedbackContent.append(line).append("\n");
            }
            textArea.setText(feedbackContent.toString());
            return true; // 파일이 존재함
        } catch (IOException e) {
            return false; // 더 이상 파일 없음
        }
    }


    // 메인 메뉴
    private void showMainMenu() {
        JFrame frame = new JFrame("메인 메뉴");
        frame.setLayout(new GridLayout(3, 1));
        frame.setSize(400, 200);

        JLabel label = new JLabel("메뉴를 선택하세요:", SwingConstants.CENTER);
        JButton goalsButton = new JButton("목표 관리");
        JButton exitButton = new JButton("종료");

        goalsButton.addActionListener(e -> {
            frame.dispose();
            manageGoals();
        });
        exitButton.addActionListener(e -> System.exit(0));

        frame.add(label);
        frame.add(goalsButton);
        frame.add(exitButton);
        frame.setVisible(true);
    }

    // 목표 관리
    private void manageGoals() {
        JFrame frame = new JFrame("목표 관리");
        frame.setLayout(new GridLayout(4, 1));  // 3에서 4로 변경하여 한 줄 더 추가
        frame.setSize(400, 200);

        JButton addButton = new JButton("목표 추가");
        JButton viewButton = new JButton("목표 보기");
        JButton exitButton = new JButton("종료하기");

        addButton.addActionListener(e -> {
            frame.dispose();
            addGoal();  // 목표 추가 화면으로 이동
        });
        viewButton.addActionListener(e -> {
            frame.dispose();
            viewGoals();  // 목표 보기 화면으로 이동
        });
        exitButton.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(frame, "정말 종료하시겠습니까?", "종료 확인", JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                System.exit(0);  // 프로그램 종료
            }
        });

        // 레이아웃에 추가
        frame.add(new JLabel("작업을 선택하세요:", SwingConstants.CENTER));
        frame.add(addButton);
        frame.add(viewButton);
        frame.add(exitButton);  // 종료하기 버튼 추가
        frame.setVisible(true);
    }


    // 목표 추가
    private void addGoal() {
        JFrame frame = createFrame("목표 추가");

        JLabel label = new JLabel("새로운 목표를 입력하세요.", SwingConstants.CENTER);
        JTextField textField = new JTextField(15);
        JButton saveButton = new JButton("저장");
        JButton backButton = new JButton("돌아가기"); // 돌아가기 버튼 추가

        saveButton.addActionListener(e -> {
            String goal = textField.getText().trim();
            if (!goal.isEmpty()) {
                saveToFile("goals.txt", userName + ": " + goal);
                JOptionPane.showMessageDialog(null, "목표가 저장되었습니다!");
            }
            textField.setText(""); // 입력 필드 초기화
        });

        backButton.addActionListener(e -> {
            frame.dispose();
            manageGoals(); // 목표 관리로 돌아가기
        });

        // 컴포넌트들을 프레임에 직접 추가
        frame.setLayout(new FlowLayout());
        frame.add(label);
        frame.add(textField);
        frame.add(saveButton);
        frame.add(backButton);

        frame.setVisible(true);
    }



    // 목표 보기
    private void viewGoals() {
        JFrame frame = createFrame("목표 보기");

        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);

        try {
            List<String> goals = Files.readAllLines(Paths.get("goals.txt"));
            for (String goal : goals) {
                textArea.append(goal + "\n");
            }
        } catch (IOException e) {
            textArea.setText("저장된 목표가 없습니다.");
        }

        JScrollPane scrollPane = new JScrollPane(textArea);
        JButton backButton = new JButton("돌아가기");

        backButton.addActionListener(e -> {
            frame.dispose();
            manageGoals(); // 목표 관리로 돌아가기
        });

        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(backButton, BorderLayout.SOUTH);
        frame.setVisible(true);
    }

    // 파일 저장
    private void saveToFile(String fileName, String content) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true))) {
            writer.write(content);
            writer.newLine();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "파일 저장 실패: " + fileName);
        }
    }

    // 간단 메시지
    private void showMessage(String title, String message, String buttonText, java.awt.event.ActionListener action) {
        JFrame frame = new JFrame(title);
        frame.setLayout(new FlowLayout());
        frame.setSize(400, 200);

        JLabel label = new JLabel(message);
        JButton button = new JButton(buttonText);

        button.addActionListener(e -> {
            frame.dispose();
            action.actionPerformed(e);
        });

        frame.add(label);
        frame.add(button);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(P_to_J::new);
    }
}
