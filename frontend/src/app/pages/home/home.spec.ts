import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { Home } from './home';
import { Auth } from '../../services/auth';

describe('Home', () => {
  let fixture: ComponentFixture<Home>;
  let token: string | null;

  beforeEach(async () => {
    token = null;

    await TestBed.configureTestingModule({
      imports: [Home],
      providers: [
        provideRouter([]),
        {
          provide: Auth,
          useValue: {
            getToken: () => token,
          },
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(Home);
  });

  it('should create', () => {
    fixture.detectChanges();

    expect(fixture.componentInstance).toBeTruthy();
  });

  it('should show login and registration links for guests', () => {
    fixture.detectChanges();

    const page = fixture.nativeElement as HTMLElement;

    expect(page.querySelector('a[href="/login"]')).not.toBeNull();
    expect(page.querySelector('a[href="/register"]')).not.toBeNull();
    expect(page.querySelector('a[href="/dashboard"]')).toBeNull();
  });

  it('should show the dashboard link when a token exists', () => {
    token = 'test-token';
    fixture.detectChanges();

    const page = fixture.nativeElement as HTMLElement;

    expect(page.querySelector('a[href="/dashboard"]')).not.toBeNull();
    expect(page.querySelector('a[href="/login"]')).toBeNull();
    expect(page.querySelector('a[href="/register"]')).toBeNull();
  });
});
