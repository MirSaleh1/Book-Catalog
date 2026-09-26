# BookCatalog API - Axtarış, Filtrləmə və Səhifələmə

Bu proyekt, böyük məlumat bazalarından düzgün altçoxluğu (subset) sürətli şəkildə çıxarmaq üçün dizayn edilmiş, tam təchizatlı Spring Boot REST API-dir. Layihənin əsas məqsədi dinamik filtrləmə, çoxsahəli sıralama (multi-field sorting), səhifələmə (pagination) əməliyyatlarını ən optimal şəkildə həyata keçirmək və arxa planda yaranan **N+1 sorğu problemini** diaqnoz edərək həll etməkdir.

## 🛠 İstifade Olunan Texnologiyalar

* **Java 17+**
* **Spring Boot 3** (Spring Web, Spring Data JPA, Validation)
* **PostgreSQL** (Performans testləri üçün real relyasiyalı məlumat bazası)
* **Flyway / Liquibase** (Məlumat bazası miqrasiyaları və 1000+ kitablıq başlanğıc (seed) data)
* **MapStruct** (Entity - DTO çevirmələri)
* **JUnit 5 & Testcontainers** (İnteqrasiya testləri)

## ✨ Əsas Xüsusiyyətlər

* **Domain Model:** `Book` (Kitab), `Author` (Yazar - Many-to-Many) və `Category` (Kateqoriya - Many-to-One) əlaqələri düzgün "owning side" və sətiraltı (lazy) yükləmə strategiyaları ilə qurulmuşdur.
* **Səhifələmə və Metadata:** `GET /api/books` endpointi `page`, `size` və `sort` parametrlərini qəbul edir. Cavabda isə (total elements, total pages, first/last) kimi səhifələmə metadataları qaytarılır. `size` parametri üçün maksimum limit təyin edilib.
* **Dinamik Filtrləmə (JPA Specifications):** Kitab adı, yazar adı, kateqoriya, nəşr ili aralığı və minimum reytinqə görə kombinasiya edilə bilən, dinamik, opsional filtrlər.
* **Təhlükəsiz Sıralama (Whitelisted Sorting):** Yalnız icazə verilmiş sütunlar (başlıq, il, reytinq) üzrə ASC/DESC sıralama aparıla bilər. Təhlükəsizliyi təmin etmək üçün ixtiyari sütun adları bloklanır (400 Bad Request).
* **DTO Proyeksiyası (No Entity Leaking):** API heç vaxt birbaşa JPA Entity obyekti qaytarmır. Bunun əvəzinə MapStruct vasitəsilə `BookSummaryResponse` (Siyahı üçün) və `BookDetailResponse` (Tək kitab üçün) DTO-ları istifadə edilir.
* **Məlumatların Doğrulanması (Validation):** Yanlış səhifə nömrələri, limitdən böyük səhifə ölçüləri və ya tanınmayan sıralama sahələri 500 əvəzinə açıq mesajla 400 Bad Request qaytarır.

---

## 🚨 N+1 Probleminin Diaqnozu və Həlli (ƏN VACİB HİSSƏ)

Bu layihənin ən kritik mərhələsi kitabları öz yazarları ilə birlikdə (Many-to-Many) gətirərkən yaranan N+1 problemini tapmaq və optimal şəkildə həll etmək olmuşdur. Problemi izləmək üçün Hibernate SQL logging (`show-sql` və `generate_statistics`) aktiv edilmişdir.

### ❌ Əvvəl: N+1 Problemi (Default Lazy Loading)

Standart JPA davranışı (Lazy Loading) ilə 50 kitablıq bir səhifəni çağırarkən verilənlər bazasına aşağıdakı sayda sorğu gedirdi:

* **Sorğu Sayı:** `1` (Kitabları gətirmək üçün) + `50` (Hər bir kitabın yazarlarını gətirmək üçün ayrı-ayrı sorğular) = **Cəmi 51 Query**
* **Nəticə:** Məlumat bazasına həddindən artıq yük düşür və API çox yavaş işləyir.

### ✅ Sonra: Həll (İki Mərhələli Sorğu - Two-Query Approach)

Yalnızca `JOIN FETCH` istifadə etdikdə səhifələmə ilə çaxnaşma yaranır və Hibernate məlumat bazası əvəzinə yaddaşda (in-memory) səhifələmə edərək `HHH000104` xəbərdarlığını verir (bu 1000+ kitab üçün fəlakətdir).
Bunun qarşısını almaq üçün problem **İki Mərhələli Sorğu (Two-Query Approach)** ilə həll edildi:

1. **Sorğu 1:** Səhifələmə və filtrləmə şərtlərinə uyğun gələn kitabların yalnız ID-lərini gətirir.
2. **Sorğu 2:** Tapılmış həmin ID-ləri `IN (...)` şərti ilə istifadə edərək, kitabları, yazarları və kateqoriyaları tək bir sorğu daxilində `JOIN FETCH` (və ya `@EntityGraph`) ilə çəkir.

* **Sorğu Sayı (Həlldən sonra):** 1 (ID-lər üçün) + 1 (Əlaqəli məlumatları gətirmək üçün) = **Cəmi 2 Query** (51 əvəzinə!)

---

## 🚀 Quraşdırma və İşəsalma

1. **Repozitoriyanı klonlayın:**
```bash
git clone https://github.com/SizinUsername/Book-Catalog.git
cd Book-Catalog

```


2. **Verilənlər Bazasını qaldırın (Docker ilə):**
Proyektdəki `docker-compose.yml` faylından istifadə edərək PostgreSQL bazasını işə salın:
```bash
docker-compose up -d

```


3. **Proyekti işə salın:**
Flyway/Liquibase avtomatik olaraq cədvəlləri yaradacaq və 1000+ test datası ilə bazanı dolduracaq.
```bash
./mvnw spring-boot:run

```



## 📖 API İstifadə Nümunələri

**Kitabların siyahısının gətirilməsi (Səhifələmə, Sıralama və Filtrləmə ilə):**

```http
GET /api/books?page=0&size=20&sort=publicationYear,desc&title=java&minRating=4.0

```

**Uğurlu Cavab (JSON):**

```json
{
  "content": [
    {
      "id": 142,
      "title": "Effective Java",
      "category": "Programming",
      "authors": ["Joshua Bloch"],
      "publicationYear": 2018,
      "rating": 4.9
    }
  ],
  "pageNo": 0,
  "pageSize": 20,
  "totalElements": 45,
  "totalPages": 3,
  "last": false
}

```
