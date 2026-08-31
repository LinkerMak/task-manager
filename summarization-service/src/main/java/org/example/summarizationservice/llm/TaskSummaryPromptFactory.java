package org.example.summarizationservice.llm;

import org.example.taskmanager.contracts.summary.TaskSummaryRequest;
import org.example.taskmanager.contracts.summary.TaskSummaryTask;
import org.example.taskmanager.contracts.summary.TaskSummaryTaskStatus;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class TaskSummaryPromptFactory {

    private static final DateTimeFormatter PERIOD_FORMATTER =
            DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm XXX");

    private static final String SYSTEM_MESSAGE = """
            Ты формируешь краткий ежедневный отчёт для пользователя TODO-приложения.

            Правила:
            1. Пиши только на русском языке.
            2. Используй исключительно данные, переданные в пользовательском сообщении.
            3. Не выдумывай задачи, сроки, причины, приоритеты, статусы или другую информацию.
            4. Структурируй ответ строго в трёх разделах:
               Выполнено
               Осталось
               Итог
            5. Если выполненных или незавершённых задач нет, явно сообщи об этом.
            6. Не используй Markdown-таблицы.
            7. Не обращайся к пользователю по имени.
            8. Максимальная длина ответа — 1 000 символов.
            """;

    public TaskSummaryPrompt create(TaskSummaryRequest request) {
        List<TaskSummaryTask> completedTasks = filterTasksFromStatus(
                request.tasks(),
                TaskSummaryTaskStatus.DONE
        );

        List<TaskSummaryTask> unfinishedTasks = filterTasksFromStatus(
                request.tasks(),
                TaskSummaryTaskStatus.TODO
        );

        String userMessage = """
                Сформируй отчёт по задачам за период: %s — %s.

                Выполненные задачи:
                %s

                Незавершённые задачи:
                %s
                """.formatted(
                PERIOD_FORMATTER.format(request.periodStart()),
                PERIOD_FORMATTER.format(request.periodEnd()),
                formatTasks(completedTasks),
                formatTasks(unfinishedTasks)
        );

        return new TaskSummaryPrompt(
                SYSTEM_MESSAGE,
                userMessage
        );
    }

    private List<TaskSummaryTask> filterTasksFromStatus(List<TaskSummaryTask> tasks, TaskSummaryTaskStatus status) {
        return tasks.stream()
                .filter(task -> task.status() == status)
                .toList();
    }

    private String formatTasks(List<TaskSummaryTask> tasks) {
        if (tasks.isEmpty()) {
            return "Нет задач.";
        }

        StringBuilder result = new StringBuilder();

        for (int index = 0; index < tasks.size(); index++) {
            TaskSummaryTask task = tasks.get(index);

            result.append(index + 1)
                    .append(". ")
                    .append(task.title());

            if (task.description() != null && !task.description().isBlank()) {
                result.append(" — ")
                        .append(task.description());
            }

            result.append(System.lineSeparator());
        }

        return result.toString().stripTrailing();
    }
}
