(define (caar x) (car (car x)))
(define (cadr x) (car (cdr x)))
(define (cdar x) (cdr (car x)))
(define (cddr x) (cdr (cdr x)))

; Some utility functions that you may find useful to implement.
(define (map proc items)
  (cond
    ((null? items) nil)
    (else (cons (proc (car items)) (map proc (cdr items))))
    )
)


(define (cons-all first rests)
  (define (proc s)
    (cons first s)
  )
  (map proc rests)
) 


(define (zip pairs)
  (define firsts (map (lambda (pair) (car pair)) pairs))
  (define seconds (map (lambda (pair) (cadr pair)) pairs))
  (list firsts seconds)
)


;;

(define (zip-2 lst1 lst2)
  (if (or (null? lst1) (null? lst2))
        nil
        (cons (list (car lst1) (car lst2)) (zip-2 (cdr lst1) (cdr lst2)))))


(define (enumerate-interval low high)
  (if (> low high)
      nil
      (cons low (enumerate-interval (+ low 1) high))))


(define (len s)
  (if (eq? s '())
    0
    (+ 1 (len (cdr s)))))


;; Problem 17
;; Returns a list of two-element lists
(define (enumerate s)
  ; BEGIN PROBLEM 17
 (define index (enumerate-interval 0 (- (len s) 1)))
 (zip-2 index s)
)
  ; END PROBLEM 17

(define change nil)

;; Problem 18
;; List all ways to make change for TOTAL with DENOMS
(define (list-change total denoms)
  ; BEGIN PROBLEM 18
  
  (cond 
    ((eq? total 0) (list '()))
    ((null? denoms) nil)
    ((< total 0) nil)
    (else (append (cons-all (car denoms) (list-change (- total (car denoms)) denoms))
                  (list-change total (cdr denoms))))
  )
)
  ; END PROBLEM 

;; Problem 19
;; Returns a function that checks if an expression is the special form FORM
(define (check-special form)
  (lambda (expr) (equal? form (car expr))))

(define lambda? (check-special 'lambda))
(define define? (check-special 'define))
(define quoted? (check-special 'quote))
(define let?    (check-special 'let))

;; Converts all let special forms in EXPR into equivalent forms using lambda
(define (let-to-lambda expr)
  (cond ((atom? expr)
         ; BEGIN PROBLEM 19
         expr
         ; END PROBLEM 19
         )
        ((quoted? expr)
         ; BEGIN PROBLEM 19
         expr
         ; END PROBLEM 19
         )
        ((or (lambda? expr)
             (define? expr))
         (let ((form   (car expr))
               (params (cadr expr))
               (body   (cddr expr)))
           ; BEGIN PROBLEM 19 
            (append (list form params) (let-to-lambda body))
           ; END PROBLEM 19
           ))
        ((let? expr)
         (let ((values (cadr expr))
               (body   (cddr expr)))
           ; BEGIN PROBLEM 19
         (define params (car (zip values)))
         (define vals (cadr (zip values)))

         (let-to-lambda(cons (append (list 'lambda params) (let-to-lambda body)) vals))
         
           ; END PROBLEM 19
         )) 
        (else
         ; BEGIN PROBLEM 19
         (map let-to-lambda expr)
         ; END PROBLEM 19
         )))
