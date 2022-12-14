import java.time.Duration;
import java.time.Instant;
import java.util.InputMismatchException;
import java.util.Scanner;
import static java.lang.Math.sqrt;

/**
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * @author NesioIV
 * @version 1.0
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 */

/*
   реализуется задача подсчёта всех простых чисел, которые меньше или
   равны заданному числу N, двумя разными по эффективности методами

   описание решения задачи приведено в файле ..\README.md 
*/
class PrimeNumSieve {

   static final int SIX = 6;  // произведение первых двух простых чисел 2*3

   /*
      подсчёт всех простых чисел по алгоритму Эратосфена
   */
   static int Eratosthenes (int N) {  // передача/получение входного числа N
      /* @param N - целое положительное число (integer)
       * @return numOfPrimes - количество простых чисел, меньших или равных N (integer) */
      int primesCounter = 0;  // счётчик простых чисел
      if (N > 0) {
         boolean[] primesArr = new boolean[N];
         for (int i = 1; i <= N; i++) {
            primesArr[i - 1] = true; // засеять массив метками простых чисел
         }
         primesArr[0] = false;
         for (int i = 2; i <= sqrt(N); i++) {
            if (primesArr[i - 1]) {
               for (int j = i; i * j <= N; j++) {
                  primesArr[i * j - 1] = false;  // снять метки у заведомо составных чисел
               }
            }
         }
         for (int i = 1; i <= N; i++) {
            if (primesArr[i - 1]) {
               primesCounter++;  // подсчитать метки простых чисел
            }
         }
      }
      return primesCounter;
   }

   /*
      подсчёт всех простых чисел по комбинированному алгоритму Эратосфена
      и "колесного решета" (wheel factorization)
   */
   static int EratosthenesWheel (int N) {  // передача/получение входного числа N
      /* @param N - целое положительное число (integer)
       * @return numOfPrimes - количество простых чисел, меньших или равных N (integer) */
      int primesCounter = 0;  // счётчик простых чисел
      if (N > 0) {
         int arrSize = N / SIX * 2 + 1;  // длина массива признаков простых чисел
         boolean[] primesArr = new boolean[arrSize];
         for (int i = 1; i <= arrSize; i++) {
            primesArr[i - 1] = true;  // засеять массив метками простых чисел
         }
         int m1, m2;  // предположительно простые числа
         int k11, k12, k21, k22;  // коэффициенты-генераторы чисел по колесному решету
         int ij;  // составные числа Эратосфена
         if (N >= SIX) {  // основная часть алгоритма
            for (int i = 5; i <= sqrt(N); i++) {
               m1 = (i + 1) / SIX;  // "распаковка" числа m1 по значению индекса i
               if (m1 * SIX - 1 == i) {
                  if (primesArr[(2 * m1 - 1) - 1]) {
                     for (int j = i; i * j <= N; j++) {
                        ij = i * j;  // генерация составных чисел по Эратосфену
                        k11 = (ij + 1) / SIX;
                        if (k11 * SIX - 1 == ij) {  // их проверка по колесному решетy k * 6 - 1
                           primesArr[(2 * k11 - 1) - 1] = false;
                        }
                        k12 = (ij - 1) / SIX;
                        if (k12 * SIX + 1 == ij) {  // их проверка по колесному решетy k * 6 = 1
                           primesArr[2 * k12 - 1] = false;
                        }
                     }
                  }
               }
               m2 = (i - 1) / SIX;  // "распаковка" числа m2 по значению индекса i
               if (m2 * SIX + 1 == i) {
                  if (primesArr[2 * m2 - 1]) {
                     for (int j = i; i * j <= N; j++) {
                        ij = i * j;  // генерация составных чисел по Эратосфену
                        k21 = (ij + 1) / SIX;
                        if (k21 * SIX - 1 == ij) {  // их проверка по колесному решетy k * 6 - 1
                           primesArr[(2 * k21 - 1) - 1] = false;
                        }
                        k22 = (ij - 1) / SIX;
                        if (k22 * SIX + 1 == ij) {  // их проверка по колесному решетy k * 6 = 1
                           primesArr[2 * k22 - 1] = false;
                        }
                     }
                  }
               }
            }
            primesCounter = 2; // учтены простые 2, 3
            for (int i = 1; i <= arrSize / 2; i++) {  // подсчёт помеченных простых чисел
               m1 = SIX * i - 1;
               m2 = SIX * i + 1;
               if (m1 <= N & primesArr[(2 * i - 1) - 1]) {
                  primesCounter++;
               }
               if (m2 <= N & primesArr[2 * i - 1]) {
                  primesCounter++;
               }
            }
         } else {  // крайний случай: определить, результат для первых трёх простых чисел
            switch (N) {
               case (5):
                  primesCounter = 3; // простые 2, 3, 5
                  break;
               case (4):
               case (3):
                  primesCounter = 2; // простые 2, 3
                  break;
               case (2):
                  primesCounter = 1;  // простое 2
                  break;
               default:
                  // простых 0
                  break;
            }
         }
      }
      return primesCounter;
   }

   /*
      главный метод класса
      - реализует консольный интерфейс с пользователем
      - принимает ввод исходных данных (значение N), контролирует
        ограничения вводимых данных на допустимость и на
        доступность необходимого объема памяти
      - вызывает два различных метода подсчёта простых чисел
      - выводит результаты работы приложения в консоль
      - использует класс java.util.Scanner
   */

   public static void main(String[] args) {

      System.out.println("\n" + "<Решето простых чисел> NesioIV, 2022" + "\n");
      int number = 0;  // задаваемая пользователем граница подсчёта простых чисел
      int primes;  // подсчитанное количество простых чисел
      int memDecrem = 1;  // предлагаемый декремент изменения проверямого объема памяти в % к number
      System.out.print("Введите целое положительное число (не более 2147483647): \n");
      while (number <= 0) {
         try {
            Scanner scan = new Scanner(System.in);
            number = scan.nextInt();
            if (number > 0) {
               System.out.println("Вы ввели число " + number + ". Выполняется проверка наличия необходимой памяти... \n");
               try {
                  boolean[] primesArr = new boolean[number];
                  System.out.println("Проверка наличия необходимой памяти прошла успешно.");
                  primesArr = null;  // освобождение памяти
                  System.gc(); // освобождение памяти
                  System.out.println("Вы ввели число " + number + ". Выполняется подсчёт простых чисел... \n");
                  System.out.println("ВНИМАНИЕ: ПОДСЧЁТ ПРОСТЫХ ЧИСЕЛ БУДЕТ ВЫПОЛНЕН 2-МЯ РАЗНЫМИ МЕТОДАМИ. \n");
               } catch (OutOfMemoryError e) {
                  System.out.println("Памяти недостаточно.");
                  System.err.println("Введите целое положительное число (предлагаемое значение - не более " + (number / 100 * (100 - memDecrem)) + ")");
                  number = -1;
               }
            }
            else {
               System.err.println("Ввод не корректен. Введите целое положительное число (не более 2147483647): ");
            }
         }
         catch (InputMismatchException e)
         {
            System.err.println("Ввод не корректен. Введите целое положительное число (не более 2147483647): ");
         }
      }
      Instant startTime = Instant.now();
      primes = Eratosthenes(number);
      Instant stopTime = Instant.now();
      System.out.printf("%-25s %-25s %-25s %-25s", "Использованный метод", "Найдено простых чисел", "Размер массива памяти,Бт", "Время исполнения,мс");
      System.out.printf("%n%-25s %-25d %-25d %-25d", "Eratosthenes", primes, number, Duration.between(startTime, stopTime).toMillis());
      startTime = Instant.now();
      primes = EratosthenesWheel(number);
      stopTime = Instant.now();
      System.out.printf("%n%-25s %-25d %-25d %-25d", "EratosthenesWheel", primes, number / SIX * 2 + 1, Duration.between(startTime, stopTime).toMillis());
      System.out.println();
      System.out.println("\nВТОРОЙ МЕТОД ИСПОЛЬЗУЕТ МЕНЬШЕ ПАМЯТИ И ВРЕМЯ ЕГО ВЫПОЛНЕНИЯ СТАНОВИТСЯ БОЛЕЕ ЭФФЕКТИВНЫМ С РОСТОМ N.");
   }
}