import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BoardSettings } from './board-settings';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { provideRouter } from '@angular/router';

describe('BoardSettings', () => {
  let component: BoardSettings;
  let fixture: ComponentFixture<BoardSettings>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BoardSettings],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        provideRouter([]),
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(BoardSettings);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
